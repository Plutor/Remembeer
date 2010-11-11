opus.Gizmo({
	name: "importExportDialog",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	t: 0,
	h: 245,
	styles: {
		zIndex: 2
	},
	chrome: [
		{
			name: "label1",
			label: "Import/Export",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 0,
			h: 39,
			styles: {
				textAlign: "center"
			}
		},
		{
			name: "expToEmail",
			ontap: "expToEmailTap",
			disabled: undefined,
			label: "Export to Email",
			type: "Palm.Mojo.Button",
			l: 64,
			w: 193,
			t: 79,
			hAlign: "center"
		},
		{
			name: "expLocally",
			ontap: "expLocallyTap",
			disabled: true,
			label: "Export Locally",
			type: "Palm.Mojo.Button",
			l: 64,
			w: 193,
			t: 139,
			hAlign: "center"
		},
		{
			name: "importButton",
			ontap: "importButtonTap",
			disabled: undefined,
			label: "Import from Local File",
			type: "Palm.Mojo.Button",
			l: 0,
			w: 255,
			t: 199,
			hAlign: "center"
		}
	]
});