/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React from 'react';

export function WarningMessage({message}) {
	return (
		<div className="autofit-row mt-2 small text-warning">
			<div className="autofit-col">
				<div className="autofit-section mr-2">
					<ClayIcon symbol="warning-full" />
				</div>
			</div>

			<div className="autofit-col autofit-col-expand">
				<div className="autofit-section">{message}</div>
			</div>
		</div>
	);
}

WarningMessage.propTypes = {
	message: PropTypes.string.isRequired,
};
