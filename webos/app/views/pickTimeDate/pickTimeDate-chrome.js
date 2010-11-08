opus.Gizmo({
	name: "pickTimeDate",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	t: 0,
	h: 241,
	styles: {
		zIndex: 2,
		bgColor: "",
		bgImage: ""
	},
	chrome: [
		{
			name: "label3",
			label: "Select the specific date and time the beer was consumed. Must be in the past.",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 0,
			h: 41,
			styles: {
				fontSize: "15px",
				textAlign: "center"
			}
		},
		{
			name: "panel1",
			layoutKind: "hbox",
			dropTarget: true,
			type: "Palm.Mojo.Panel",
			l: 0,
			t: 180,
			h: 60,
			controls: [
				{
					name: "label1",
					label: "DATE",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 61,
					t: 15,
					h: 35,
					styles: {
						padding: "5",
						textColor: "blue",
						fontSize: "17px"
					}
				},
				{
					name: "datePicker",
					modelName: "dateModel",
					date: "Mon Sep 13 2010 11:43:09 GMT-0400 (Eastern Daylight Time)",
					label: "",
					labelPlacement: "right",
					type: "Palm.Mojo.DatePicker",
					l: 61,
					w: 261,
					t: 1
				}
			]
		},
		{
			name: "panel2",
			layoutKind: "hbox",
			dropTarget: true,
			type: "Palm.Mojo.Panel",
			l: 0,
			t: 60,
			h: 59,
			controls: [
				{
					name: "label2",
					label: "TIME",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 61,
					t: 11,
					h: 39,
					styles: {
						margin: "5",
						textColor: "blue",
						fontSize: "17px"
					}
				},
				{
					name: "timePicker",
					modelName: "timeModel",
					time: "Mon Sep 13 2010 08:53:01 GMT-0400 (Eastern Daylight Time)",
					label: "",
					labelPlacement: "right",
					type: "Palm.Mojo.TimePicker",
					l: 61,
					w: 260,
					t: -1,
					hAlign: "left",
					styles: {
						textColor: ""
					}
				}
			]
		},
		{
			name: "button",
			ontap: "okButtonTap",
			disabled: undefined,
			label: "OK",
			type: "Palm.Mojo.Button",
			l: 109,
			w: 100,
			t: 180,
			hAlign: "center"
		}
	]
});