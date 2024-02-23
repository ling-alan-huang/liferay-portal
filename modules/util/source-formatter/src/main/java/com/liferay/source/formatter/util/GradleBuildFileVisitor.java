/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
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
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.syntax.Token;

/**
 * @author Kevin Lee
 */
public class GradleBuildFileVisitor extends CodeVisitorSupport {

	public List<GradleDependency> getBuildScriptDependencies() {
		return _buildScriptDependencies;
	}

	public int getDependenciesLastLineNumber() {
		return _dependenciesLastLineNumber;
	}

	public int getDependenciesLineNumber() {
		return _dependenciesLineNumber;
	}

	public List<GradleDependency> getGradleDependencies() {
		return _gradleDependencies;
	}

	public boolean isDependenciesWithIfElse() {
		return _dependenciesWithIfElse;
	}

	@Override
	public void visitArgumentlistExpression(
		ArgumentListExpression argumentListExpression) {

		if (!_inDependencies && !_inBuildScript) {
			return;
		}

		List<Expression> expressions = argumentListExpression.getExpressions();

		if (expressions.size() == 1) {
			if (expressions.get(0) instanceof ConstantExpression) {
				ConstantExpression constantExpression =
					(ConstantExpression)expressions.get(0);

				String text = constantExpression.getText();

				String[] textParts = text.split(":");

				if ((textParts.length >= 3) && _inDependencies) {
					GradleDependency gradleDependency = new GradleDependency(
						_configuration, textParts[0], textParts[1],
						textParts[2], _methodCallLineNumber,
						_methodCallLastLineNumber);

					if (_inBuildScript) {
						_buildScriptDependencies.add(gradleDependency);
					}
					else {
						_gradleDependencies.add(gradleDependency);
					}
				}
			}
			else if (expressions.get(0) instanceof MethodCallExpression) {
				MethodCallExpression methodCallExpression =
					(MethodCallExpression)expressions.get(0);

				Expression objectExpression =
					methodCallExpression.getObjectExpression();

				String variable = null;

				if ((objectExpression instanceof VariableExpression) &&
					!Objects.equals(objectExpression.getText(), "this")) {

					variable = objectExpression.getText();
				}
				else if (objectExpression instanceof MethodCallExpression) {
					variable = _getTextFromExpression(objectExpression);
				}

				String methodName = methodCallExpression.getMethodAsString();

				Expression argumentsExpression =
					methodCallExpression.getArguments();

				if (argumentsExpression instanceof ArgumentListExpression) {
					ArgumentListExpression anotherArgumentListExpression =
						(ArgumentListExpression)argumentsExpression;

					List<Expression> otherExpressions =
						anotherArgumentListExpression.getExpressions();

					List<String> argumentList = null;

					if (!otherExpressions.isEmpty()) {
						argumentList = new ArrayList<>();
					}

					for (Expression expression : otherExpressions) {
						argumentList.add(_getTextFromExpression(expression));
					}

					GradleDependency gradleDependency =
						new MethodGradleDependency(
							_configuration, null, null, null, methodName,
							variable, argumentList, null);

					_gradleDependencies.add(gradleDependency);

					return;
				}
				else if (argumentsExpression instanceof TupleExpression) {
					TupleExpression tupleExpression =
						(TupleExpression)argumentsExpression;

					List<Expression> expressionList =
						tupleExpression.getExpressions();

					if (expressionList.get(0) instanceof
							NamedArgumentListExpression) {

						NamedArgumentListExpression
							namedArgumentListExpression =
								(NamedArgumentListExpression)expressionList.get(
									0);

						List<MapEntryExpression> mapEntryExpressionList =
							namedArgumentListExpression.
								getMapEntryExpressions();

						Map<String, String> keyValues = new HashMap<>();

						for (MapEntryExpression mapEntryExpression :
								mapEntryExpressionList) {

							Expression keyExpression =
								mapEntryExpression.getKeyExpression();

							String key = keyExpression.getText();

							keyValues.put(
								key,
								_getTextFromExpression(
									mapEntryExpression.getValueExpression()));
						}

						GradleDependency gradleDependency =
							new MethodGradleDependency(
								_configuration, null, null, null, methodName,
								variable, null, keyValues);

						_gradleDependencies.add(gradleDependency);

						return;
					}
				}
			}
		}
		else if (expressions.size() == 2) {
			if ((expressions.get(0) instanceof MapExpression) &&
				(expressions.get(1) instanceof VariableExpression)) {

				VariableExpression variableExpression =
					(VariableExpression)expressions.get(1);

				if (Objects.equals(variableExpression.getText(), "optional")) {
					_optional = true;

					MapExpression mapExpression =
						(MapExpression)expressions.get(0);

					visitMapExpression(mapExpression);

					_optional = false;

					return;
				}
			}
		}

		super.visitArgumentlistExpression(argumentListExpression);
	}

	@Override
	public void visitBlockStatement(BlockStatement blockStatement) {
		if (_inDependencies) {
			_blockStatementStack.push(true);

			super.visitBlockStatement(blockStatement);

			_blockStatementStack.pop();
		}
		else {
			super.visitBlockStatement(blockStatement);
		}
	}

	@Override
	public void visitIfElse(IfStatement ifElse) {
		if (_inDependencies) {
			_dependenciesWithIfElse = true;
		}

		super.visitIfElse(ifElse);
	}

	@Override
	public void visitMapExpression(MapExpression mapExpression) {
		Map<String, String> keyValues = new HashMap<>();

		boolean gav = false;

		for (MapEntryExpression mapEntryExpression :
				mapExpression.getMapEntryExpressions()) {

			Expression keyExpression = mapEntryExpression.getKeyExpression();

			String key = keyExpression.getText();

			keyValues.put(
				key,
				_getTextFromExpression(
					mapEntryExpression.getValueExpression()));

			if (StringUtil.equalsIgnoreCase(key, "group")) {
				gav = true;
			}
		}

		if (gav && _inDependencies) {
			GradleDependency gradleDependency = new GradleDependency(
				_configuration, keyValues.get("group"), keyValues.get("name"),
				keyValues.get("version"), _methodCallLineNumber,
				_methodCallLastLineNumber);

			if (_inBuildScript) {
				_buildScriptDependencies.add(gradleDependency);
			}
			else {
				_gradleDependencies.add(gradleDependency);
			}
		}

		if (gav) {
			if (_optional) {
				GradleDependency gradleDependency =
					new OptionalGradleDependency(
						_configuration, keyValues.get("group"),
						keyValues.get("name"), keyValues.get("version"),
						_methodCallLineNumber, _methodCallLastLineNumber);

				_gradleDependencies.add(gradleDependency);
			}
			else if ((keyValues.size() > 3) ||
					 keyValues.containsKey("transitive")) {

				GradleDependency gradleDependency = new ExtendGradleDependency(
					_configuration, keyValues.get("group"),
					keyValues.get("name"), keyValues.get("version"), keyValues);

				_gradleDependencies.add(gradleDependency);
			}
			else if (_inDependencies) {
				GradleDependency gradleDependency = new GradleDependency(
					_configuration, keyValues.get("group"),
					keyValues.get("name"), keyValues.get("version"),
					_methodCallLineNumber, _methodCallLastLineNumber);

				if (_inBuildScript) {
					_buildScriptDependencies.add(gradleDependency);
				}
				else {
					_gradleDependencies.add(gradleDependency);
				}

				if ((_blockStatementStack.size() == 2) &&
					!_gradleDependencies.isEmpty()) {

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

		super.visitMapExpression(mapExpression);
	}

	@Override
	public void visitMethodCallExpression(
		MethodCallExpression methodCallExpression) {

		_methodCallLineNumber = methodCallExpression.getLineNumber();
		_methodCallLastLineNumber = methodCallExpression.getLastLineNumber();

		if (_methodCallLineNumber > _buildScriptLastLineNumber) {
			_inBuildScript = false;
		}

		if (_methodCallLineNumber > _dependenciesLastLineNumber) {
			_inDependencies = false;
		}

		String methodName = methodCallExpression.getMethodAsString();

		if (methodName.equals("buildscript")) {
			_inBuildScript = true;
			_buildScriptLastLineNumber =
				methodCallExpression.getLastLineNumber();
		}

		if (methodName.equals("dependencies")) {
			_inDependencies = true;
			_dependenciesLineNumber = methodCallExpression.getLineNumber();
			_dependenciesLastLineNumber =
				methodCallExpression.getLastLineNumber();
		}

		if (_inDependencies &&
			(_blockStatementStack.isEmpty() ? false :
				_blockStatementStack.peek())) {

			_configuration = methodName;
		}

		super.visitMethodCallExpression(methodCallExpression);
	}

	private String _getTextFromExpression(Expression expression) {
		StringBundler sb = new StringBundler();

		if (expression instanceof MethodCallExpression) {
			MethodCallExpression methodCallExpression =
				(MethodCallExpression)expression;

			Expression objectExpression =
				methodCallExpression.getObjectExpression();

			String variable = _getTextFromExpression(objectExpression);

			if (!Objects.equals(variable, "this")) {
				sb.append(objectExpression.getText());

				sb.append(".");
			}

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
			if (Objects.equals(expression.getText(), "false") ||
				Objects.equals(expression.getText(), "true")) {

				sb.append(expression.getText());
			}
			else {
				sb.append("\"");
				sb.append(expression.getText());
				sb.append("\"");
			}
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

	private final Stack<Boolean> _blockStatementStack = new Stack<>();
	private final List<GradleDependency> _buildScriptDependencies =
		new ArrayList<>();
	private int _buildScriptLastLineNumber = -1;
	private String _configuration;
	private int _dependenciesLastLineNumber = -1;
	private int _dependenciesLineNumber = -1;
	private boolean _dependenciesWithIfElse;
	private final List<GradleDependency> _gradleDependencies =
		new ArrayList<>();
	private boolean _inBuildScript;
	private boolean _inDependencies;
	private int _methodCallLastLineNumber = -1;
	private int _methodCallLineNumber = -1;
	private final Stack<String> _methodCallStack = new Stack<>();
	private boolean _optional;

}