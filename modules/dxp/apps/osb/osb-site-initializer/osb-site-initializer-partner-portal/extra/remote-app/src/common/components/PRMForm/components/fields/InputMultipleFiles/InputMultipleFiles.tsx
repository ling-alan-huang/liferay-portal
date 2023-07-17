/**
 * SPDX-FileCopyrightText: © 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {FormikContextType} from 'formik';
import {useDropzone} from 'react-dropzone';

import MDFClaim from '../../../../../interfaces/mdfClaim';
import PRMFormFieldProps from '../common/interfaces/prmFormFieldProps';
import PRMFormFieldStateProps from '../common/interfaces/prmFormFieldStateProps';
interface IProps {
	onAccept: (value: File[]) => void;
}

const InputMultipleFiles = ({
	description,
	field,
	label,
	onAccept,
	required,
}: PRMFormFieldProps &
	PRMFormFieldStateProps<File[]> &
	Pick<FormikContextType<MDFClaim>, 'setFieldValue'> &
	IProps) => {
	const {getInputProps, getRootProps, open} = useDropzone({
		noClick: true,
		noKeyboard: true,
		onDrop: (acceptedFiles) => {
			onAccept(acceptedFiles);
		},
	});

	return (
		<div className="d-flex flex-column">
			{label && (
				<label className="font-weight-semi-bold">
					{label}

					{required && <span className="text-danger">*</span>}
				</label>
			)}

			<div
				{...getRootProps({
					className:
						'bg-white d-flex align-items-center rounded flex-column border-neutral-4 border',
				})}
			>
				<ClayInput
					{...getInputProps({
						name: field.name,
						required,
					})}
				/>

				<div className="align-items-center d-flex flex-column p-3">
					<p className="font-weight-bold text-neutral-10 text-paragraph">
						{description}
					</p>

					<p className="text-neutral-7 w-75">
						Only files with the following extensions wil be
						accepted: doc, docx.jpeg, jpg, pdf, tif, tiff
					</p>

					<p className="font-weight-bold text-neutral-7">Or</p>

					<button
						className="btn btn-secondary"
						onClick={open}
						type="button"
					>
						Select Files
					</button>
				</div>
			</div>
		</div>
	);
};

export default InputMultipleFiles;
