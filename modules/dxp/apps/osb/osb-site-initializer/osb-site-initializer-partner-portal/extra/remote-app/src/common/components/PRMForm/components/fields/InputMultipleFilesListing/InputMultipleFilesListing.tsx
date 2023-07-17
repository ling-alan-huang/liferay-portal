/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PRMForm from '../../..';
import PRMFormik from '../../../../PRMFormik';
import ListFiles from './components/ListFiles';

interface IProps {
	description: string;
	label: string;
	name: string;
	onAccept: (value: File[]) => void;
	value?: File[] | Object[];
}

const InputMultipleFilesListing = ({
	description,
	label,
	name,
	onAccept,
	value,
}: IProps) => (
	<>
		<PRMFormik.Field
			component={PRMForm.InputMultipleFiles}
			description={description}
			label={label}
			onAccept={onAccept}
		/>

		{value && (
			<PRMFormik.Array component={ListFiles} files={value} name={name} />
		)}
	</>
);

export default InputMultipleFilesListing;
