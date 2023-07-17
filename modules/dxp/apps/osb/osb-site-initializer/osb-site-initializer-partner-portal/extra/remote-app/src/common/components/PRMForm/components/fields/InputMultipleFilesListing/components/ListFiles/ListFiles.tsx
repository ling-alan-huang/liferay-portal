/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ArrayHelpers} from 'formik';

interface IProps {
	arrayHelpers: ArrayHelpers;
	files: File[];
}

const ListFiles = ({arrayHelpers, files}: IProps) => {
	return (
		<div>
			{files.map((file, index) => (
				<div
					className="align-items-center bg-neutral-0 border border-neutral-4 d-flex justify-content-between mt-2 p-2 rounded-xs shadow-sm"
					key={index}
				>
					<div className="font-weight-bold">
						<div className="text-neutral-8">{file.name}</div>
					</div>

					<ClayButtonWithIcon
						className="text-neutral-7"
						displayType={null}
						onClick={() => arrayHelpers.remove(index)}
						small
						symbol="times-circle"
					/>
				</div>
			))}
		</div>
	);
};
export default ListFiles;
