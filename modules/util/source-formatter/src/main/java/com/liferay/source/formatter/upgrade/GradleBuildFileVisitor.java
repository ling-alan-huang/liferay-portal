/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.source.formatter.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.gradle.ExcludeRuleGradleDependency;
import com.liferay.source.formatter.gradle.ExternalGradleDependency;
import com.liferay.source.formatter.gradle.FileGradleDependency;
import com.liferay.source.formatter.gradle.MethodGradleDependency;
import com.liferay.source.formatter.gradle.NoTransitiveGradleDependency;
import com.liferay.source.formatter.gradle.ProjectGradleDependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.TreeSet;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.syntax.Token;

/**
 * @author Kevin Lee
 * @author Seiphon Wang
 */
public class GradleBuildFileVisitor extends CodeVisitorSupport {

	public int getDependenciesLastLineNumber() {
		return _dependenciesLastLineNumber;
	}

	public int getDependenciesLineNumber() {
		return _dependenciesLineNumber;
	}

	public List<GradleDependency> getGradleDependencies() {
		return _gradleDependencies;
	}

	@Override
	public void visitArgumentlistExpression(
		ArgumentListExpression argumentListExpression) {

		if (!_inDependencies) {
			return;
		}

		List<Expression> expressions = argumentListExpression.getExpressions();

		if (expressions.size() == 1) {
			if (expressions.get(0) instanceof ConstantExpression) {
				ConstantExpression constantExpression =
					(ConstantExpression)expressions.get(0);

				String text = constantExpression.getText();

				if (!_methodCallStack.empty() &&
					Objects.equals(_methodCallStack.peek(), "project")) {

					GradleDependency gradleDependency =
						new ProjectGradleDependency(
							_configuration, null, null, null, text, null, null,
							null);

					_gradleDependencies.add(gradleDependency);

					return;
				}

				String[] textParts = text.split(":");

				if ((textParts.length >= 3) &&
					!_isSpecialMethodName(_configuration)) {

					GradleDependency gradleDependency = new GradleDependency(
						_configuration, textParts[0], textParts[1],
						textParts[2], _methodCallLineNumber,
						_methodCallLastLineNumber);

					_gradleDependencies.add(gradleDependency);

					return;
				}
			}
			else if (expressions.get(0) instanceof MethodCallExpression) {
				MethodCallExpression methodCallExpression =
					(MethodCallExpression)expressions.get(0);

				String methodName = methodCallExpression.getMethodAsString();

				if (Objects.equals(methodName, "files")) {
					Expression argumentsExpression =
						methodCallExpression.getArguments();

					List<String> argList = new ArrayList<>();

					if (argumentsExpression instanceof ArgumentListExpression) {
						ArgumentListExpression anotherArgumentListExpression =
							(ArgumentListExpression)argumentsExpression;

						List<Expression> otherExpressions =
							anotherArgumentListExpression.getExpressions();

						for (Expression expression : otherExpressions) {
							if (expression instanceof ConstantExpression) {
								ConstantExpression constantExpression =
									(ConstantExpression)expression;

								argList.add(constantExpression.getText());
							}
						}
					}

					Expression objectExpression =
						methodCallExpression.getObjectExpression();

					if (objectExpression instanceof MethodCallExpression) {
						MethodCallExpression calledMethodExpression =
							(MethodCallExpression)objectExpression;

						Expression anotherArgumentsExpression =
							calledMethodExpression.getArguments();

						String calledMethodName =
							calledMethodExpression.getMethodAsString();

						if ((anotherArgumentsExpression instanceof
								ArgumentListExpression) &&
							Objects.equals(calledMethodName, "project")) {

							ArgumentListExpression
								anotherArgumentListExpression =
									(ArgumentListExpression)
										anotherArgumentsExpression;

							List<Expression> otherExpressions =
								anotherArgumentListExpression.getExpressions();

							if (otherExpressions.get(0) instanceof
									ConstantExpression) {

								ConstantExpression constantExpression =
									(ConstantExpression)otherExpressions.get(0);

								String ohterArgs = constantExpression.getText();

								GradleDependency gradleDependency =
									new ProjectGradleDependency(
										_configuration, null, null, null,
										ohterArgs, null, methodName,
										argList.get(0));

								_gradleDependencies.add(gradleDependency);

								return;
							}
						}
					}

					GradleDependency gradleDependency =
						new FileGradleDependency(
							_configuration, null, null, null, argList, null,
							null, null, null);

					_gradleDependencies.add(gradleDependency);

					return;
				}

				if (Objects.equals(methodName, "fileTree")) {
					Expression argumentsExpression =
						methodCallExpression.getArguments();

					if (argumentsExpression instanceof ArgumentListExpression) {
						ArgumentListExpression anotherArgumentListExpression =
							(ArgumentListExpression)argumentsExpression;

						List<Expression> otherExpressions =
							anotherArgumentListExpression.getExpressions();

						if (otherExpressions.get(0) instanceof
								ConstantExpression) {

							ConstantExpression constantExpression =
								(ConstantExpression)otherExpressions.get(0);

							String ohterArgs = constantExpression.getText();

							GradleDependency gradleDependency =
								new FileGradleDependency(
									_configuration, null, null, null, null,
									ohterArgs, null, null, null);

							_gradleDependencies.add(gradleDependency);

							return;
						}
					}
				}

				if (!_isSpecialMethodName(methodName)) {
					GradleDependency gradleDependency =
						new MethodGradleDependency(
							_configuration, null, null, null, methodName);

					_gradleDependencies.add(gradleDependency);

					return;
				}
			}
		}

		super.visitArgumentlistExpression(argumentListExpression);
	}

	@Override
	public void visitBlockStatement(BlockStatement blockStatement) {
		if (_inDependencies) {
			_numberOfBlocks++;

			super.visitBlockStatement(blockStatement);

			_numberOfBlocks--;
		}
		else {
			super.visitBlockStatement(blockStatement);
		}
	}

	@Override
	public void visitMapExpression(MapExpression mapExpression) {
		if (!_inDependencies) {
			return;
		}

		Map<String, String> keyValues = new HashMap<>();

		boolean gav = false;
		boolean project = false;
		boolean ext = false;
		boolean withTranstive = false;

		for (MapEntryExpression mapEntryExpression :
				mapExpression.getMapEntryExpressions()) {

			Expression keyExpression = mapEntryExpression.getKeyExpression();

			String key = keyExpression.getText();

			keyValues.put(
				key,
				_getTextFromExpression(
					mapEntryExpression.getValueExpression()));

			if (StringUtil.equalsIgnoreCase(key, "ext")) {
				ext = true;
			}

			if (StringUtil.equalsIgnoreCase(key, "group")) {
				gav = true;
			}

			if (StringUtil.equalsIgnoreCase(key, "path")) {
				project = true;
			}

			if (StringUtil.equalsIgnoreCase(key, "transitive")) {
				withTranstive = true;
			}
		}

		if (gav) {
			if (withTranstive) {
				GradleDependency gradleDependency =
					new NoTransitiveGradleDependency(
						_configuration, keyValues.get("group"),
						keyValues.get("name"), keyValues.get("version"));

				_gradleDependencies.add(gradleDependency);
			}
			else if (ext) {
				GradleDependency gradleDependency =
					new ExternalGradleDependency(
						_configuration, keyValues.get("classifier"),
						keyValues.get("ext"), keyValues.get("group"),
						keyValues.get("name"), keyValues.get("version"));

				_gradleDependencies.add(gradleDependency);
			}
			else {
				GradleDependency gradleDependency = new GradleDependency(
					_configuration, keyValues.get("group"),
					keyValues.get("name"), keyValues.get("version"),
					_methodCallLineNumber, _methodCallLastLineNumber);

				if (_numberOfBlocks == 2) {
					gradleDependency.setConfiguration(_methodCallStack.peek());

					gradleDependency.setName(keyValues.get("module"));

					GradleDependency lastGradleDependency =
						_gradleDependencies.get(_gradleDependencies.size() - 1);

					if (lastGradleDependency instanceof
							ExcludeRuleGradleDependency) {

						ExcludeRuleGradleDependency excludeRuleDependency =
							(ExcludeRuleGradleDependency)lastGradleDependency;

						excludeRuleDependency.addExcludeDependency(
							gradleDependency);

						return;
					}

					ExcludeRuleGradleDependency excludeRuleDependency =
						new ExcludeRuleGradleDependency(
							lastGradleDependency.getConfiguration(),
							lastGradleDependency.getGroup(),
							lastGradleDependency.getName(),
							lastGradleDependency.getVersion(), new TreeSet<>());

					excludeRuleDependency.addExcludeDependency(
						gradleDependency);

					_gradleDependencies.remove(_gradleDependencies.size() - 1);

					_gradleDependencies.add(excludeRuleDependency);

					return;
				}

				_gradleDependencies.add(gradleDependency);
			}
		}
		else if (project) {
			GradleDependency gradleDependency = new ProjectGradleDependency(
				_configuration, null, null, null, keyValues.get("path"),
				keyValues.get("configuration"), null, null);

			_gradleDependencies.add(gradleDependency);

			return;
		}
		else if (Objects.equals(_methodCallStack.peek(), "fileTree")) {
			GradleDependency gradleDependency = new FileGradleDependency(
				_configuration, null, null, null, null, keyValues.get("dir"),
				keyValues.get("builtBy"), keyValues.get("include"),
				keyValues.get("excludes"));

			_gradleDependencies.add(gradleDependency);

			return;
		}

		super.visitMapExpression(mapExpression);
	}

	@Override
	public void visitMethodCallExpression(
		MethodCallExpression methodCallExpression) {

		_methodCallLineNumber = methodCallExpression.getLineNumber();
		_methodCallLastLineNumber = methodCallExpression.getLastLineNumber();

		if (_methodCallLineNumber > _dependenciesLastLineNumber) {
			_inDependencies = false;
		}

		String methodName = methodCallExpression.getMethodAsString();

		if (methodName.equals("dependencies")) {
			_inDependencies = true;
			_dependenciesLineNumber = methodCallExpression.getLineNumber();
			_dependenciesLastLineNumber =
				methodCallExpression.getLastLineNumber();
		}

		if (_inDependencies && (_numberOfBlocks > 0)) {
			if (_numberOfBlocks > 2) {

				// Assume all dependencies are initialized within the second
				// level of "dependencies" block

				return;
			}

			_methodCallStack.push(methodName);

			_configuration = methodName;

			if (!Objects.equals(_methodCallStack.get(0), _configuration)) {
				_configuration = _methodCallStack.get(0);
			}

			super.visitMethodCallExpression(methodCallExpression);

			_configuration = null;

			_methodCallStack.pop();
		}
		else {
			super.visitMethodCallExpression(methodCallExpression);
		}
	}

	private String _getTextFromExpression(Expression expression) {
		StringBundler sb = new StringBundler();

		if (expression instanceof MethodCallExpression) {
			MethodCallExpression methodCallExpression =
				(MethodCallExpression)expression;

			Expression objectExpression =
				methodCallExpression.getObjectExpression();

			sb.append(objectExpression.getText());

			sb.append(".");
			sb.append(methodCallExpression.getMethodAsString());
			sb.append("(");

			ArgumentListExpression arguments =
				(ArgumentListExpression)methodCallExpression.getArguments();

			List<Expression> expressionList = arguments.getExpressions();

			sb.append(_getTextFromExpressionList(expressionList));

			sb.append(")");
		}
		else if (expression instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression)expression;

			sb.append(
				_getTextFromExpression(binaryExpression.getLeftExpression()));

			Token operation = binaryExpression.getOperation();

			sb.append(" ");
			sb.append(operation.getText());
			sb.append(" ");

			sb.append(
				_getTextFromExpression(binaryExpression.getRightExpression()));
		}
		else if (expression instanceof ConstantExpression) {
			sb.append("\"");
			sb.append(expression.getText());
			sb.append("\"");
		}
		else if (expression instanceof PropertyExpression) {
			PropertyExpression propertyExpression =
				(PropertyExpression)expression;

			sb.append(propertyExpression.getText());
		}
		else if (expression instanceof GStringExpression) {
			GStringExpression gStringExpression = (GStringExpression)expression;

			sb.append("\"");

			List<ConstantExpression> stringExpressions =
				gStringExpression.getStrings();

			List<Expression> valueExpressions = gStringExpression.getValues();

			for (int i = 0; i < valueExpressions.size(); i++) {
				ConstantExpression stringExpression = stringExpressions.get(i);

				sb.append(stringExpression.getText());

				sb.append("${");
				sb.append(_getTextFromExpression(valueExpressions.get(i)));
				sb.append("}");
			}

			ConstantExpression lastStringExpression = stringExpressions.get(
				stringExpressions.size() - 1);

			sb.append(lastStringExpression.getText());

			sb.append("\"");
		}
		else if (expression instanceof VariableExpression) {
			VariableExpression variableExpression =
				(VariableExpression)expression;

			sb.append(variableExpression.getText());
		}
		else if (expression instanceof ConstructorCallExpression) {
			ConstructorCallExpression constructorCallExpression =
				(ConstructorCallExpression)expression;

			sb.append("new ");
			sb.append(constructorCallExpression.getType());
			sb.append("(");

			ArgumentListExpression arguments =
				(ArgumentListExpression)
					constructorCallExpression.getArguments();

			sb.append(_getTextFromExpressionList(arguments.getExpressions()));

			sb.append(")");
		}
		else if (expression instanceof ListExpression) {
			ListExpression listExpression = (ListExpression)expression;

			sb.append("[");
			sb.append(
				_getTextFromExpressionList(listExpression.getExpressions()));
			sb.append("]");
		}
		else {
			sb.append(expression.getText());
		}

		return sb.toString();
	}

	private String _getTextFromExpressionList(List<Expression> expressions) {
		StringBundler sb = new StringBundler();

		for (int i = 0; i < expressions.size(); i++) {
			Expression expression = expressions.get(i);

			sb.append(_getTextFromExpression(expression));

			if (i != (expressions.size() - 1)) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	private boolean _isSpecialMethodName(String methodName) {
		List<String> possibleMethodName = Arrays.asList(
			"project", "files", "fileTree");

		return possibleMethodName.contains(methodName);
	}

	private String _configuration;
	private int _dependenciesLastLineNumber = -1;
	private int _dependenciesLineNumber = -1;
	private final List<GradleDependency> _gradleDependencies =
		new ArrayList<>();
	private boolean _inDependencies;
	private int _methodCallLastLineNumber = -1;
	private int _methodCallLineNumber = -1;
	private final Stack<String> _methodCallStack = new Stack<>();
	private int _numberOfBlocks;

}