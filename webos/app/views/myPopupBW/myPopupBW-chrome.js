opus.Gizmo({
	name: "myPopupBW",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	l: 0,
	t: 0,
	h: 211,
	styles: {
		zIndex: 2,
		bgImage: "",
		bgColor: "black"
	},
	chrome: [
		{
			name: "label1",
			label: "How's That Beer?",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 0,
			h: 24,
			styles: {
				textAlign: "center",
				textColor: "white"
			}
		},
		{
			name: "label2",
			label: "Take a minute and rate it in Remembeer.",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 28,
			h: 20,
			styles: {
				textColor: "white",
				fontSize: "16px",
				textAlign: "center"
			}
		},
		{
			name: "button1",
			ontap: "okButtonTap",
			disabled: undefined,
			buttonClass: "affirmative",
			label: "OK",
			type: "Palm.Mojo.Button",
			l: 0,
			w: 117,
			t: 54,
			h: "50",
			hAlign: "center"
		},
		{
			name: "button2",
			ontap: "laterButtonTap",
			disabled: undefined,
			label: "Try Again Later",
			type: "Palm.Mojo.Button",
			l: 62,
			w: 197,
			t: 104,
			h: "50",
			hAlign: "center"
		},
		{
			name: "button3",
			ontap: "dismissButtonTap",
			disabled: undefined,
			buttonClass: "negative",
			label: "Dismiss",
			type: "Palm.Mojo.Button",
			l: 82,
			w: 156,
			t: 154,
			hAlign: "center"
		}
	]
});