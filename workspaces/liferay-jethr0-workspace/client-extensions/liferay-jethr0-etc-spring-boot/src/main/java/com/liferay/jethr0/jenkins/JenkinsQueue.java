/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jethr0.jenkins;

import com.liferay.jethr0.build.Build;
import com.liferay.jethr0.build.queue.BuildQueue;
import com.liferay.jethr0.build.repository.BuildRepository;
import com.liferay.jethr0.build.repository.BuildRunRepository;
import com.liferay.jethr0.build.run.BuildRun;
import com.liferay.jethr0.jenkins.node.JenkinsNode;
import com.liferay.jethr0.jenkins.repository.JenkinsNodeRepository;
import com.liferay.jethr0.jenkins.repository.JenkinsServerRepository;
import com.liferay.jethr0.jenkins.server.JenkinsServer;
import com.liferay.jethr0.jms.JMSEventHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michael Hashimoto
 */
@Configuration
public class JenkinsQueue {

	public void initialize() {
		if ((_jenkinsServerURLs != null) && !_jenkinsServerURLs.isEmpty()) {
			for (String jenkinsServerURL : _jenkinsServerURLs.split(",")) {
				JenkinsServer jenkinsServer = _jenkinsServerRepository.getByURL(
					jenkinsServerURL);

				if (jenkinsServer != null) {
					continue;
				}

				jenkinsServer = _jenkinsServerRepository.add(jenkinsServerURL);

				_jenkinsNodeRepository.addAll(jenkinsServer);
			}
		}

		for (JenkinsServer jenkinsServer : _jenkinsServerRepository.getAll()) {
			for (JenkinsNode jenkinsNode :
					_jenkinsNodeRepository.getAll(jenkinsServer)) {

				jenkinsServer.addJenkinsNode(jenkinsNode);

				jenkinsNode.setJenkinsServer(jenkinsServer);
			}

			jenkinsServer.update();
		}

		invoke();
	}

	public void invoke() {
		for (JenkinsServer jenkinsServer : _jenkinsServerRepository.getAll()) {
			for (JenkinsNode jenkinsNode : jenkinsServer.getJenkinsNodes()) {
				if (!jenkinsNode.isAvailable()) {
					continue;
				}

				Build build = _buildQueue.nextBuild(jenkinsNode);

				if (build == null) {
					continue;
				}

				build.setState(Build.State.QUEUED);

				BuildRun buildRun = _buildRunRepository.add(
					build, BuildRun.State.QUEUED);

				_jmsEventHandler.send(
					String.valueOf(buildRun.getInvokeJSONObject()));

				_buildRepository.update(build);
				_buildRunRepository.update(buildRun);
			}
		}
	}

	public void setJmsEventHandler(JMSEventHandler jmsEventHandler) {
		_jmsEventHandler = jmsEventHandler;
	}

	@Autowired
	private BuildQueue _buildQueue;

	@Autowired
	private BuildRepository _buildRepository;

	@Autowired
	private BuildRunRepository _buildRunRepository;

	@Autowired
	private JenkinsNodeRepository _jenkinsNodeRepository;

	@Autowired
	private JenkinsServerRepository _jenkinsServerRepository;

	@Value("${jenkins.server.urls}")
	private String _jenkinsServerURLs;

	private JMSEventHandler _jmsEventHandler;

}