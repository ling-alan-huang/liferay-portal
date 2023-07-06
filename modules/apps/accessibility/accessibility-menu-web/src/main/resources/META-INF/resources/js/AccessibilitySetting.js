/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToggle} from '@clayui/form';
import PropTypes from 'prop-types';
import React from 'react';

const KEY_EVENT = 'Enter';

const AccessibilitySetting = ({
	className,
	disabled,
	label,
	onChange,
	value,
}) => (
	<li className={className}>
		<ClayToggle
			disabled={disabled}
			label={label}
			onKeyDown={(event) => {
				if (!disabled && event.key === KEY_EVENT) {
					onChange(!value);
				}
			}}
			onToggle={onChange}
			toggled={value}
		/>
	</li>
);

AccessibilitySetting.propTypes = {
	className: PropTypes.string,
	disabled: PropTypes.bool,
	label: PropTypes.string,
	onChange: PropTypes.func.isRequired,
	value: PropTypes.bool,
};

export default AccessibilitySetting;
