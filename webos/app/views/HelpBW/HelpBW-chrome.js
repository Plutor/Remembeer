opus.Gizmo({
	name: "HelpBW",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgImage: "",
		bgColor: "black"
	},
	chrome: [
		{
			name: "header1",
			ontap: "showAppMenu",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			t: 52
		},
		{
			name: "heading",
			label: "xxx",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 50,
			h: 35,
			styles: {
				textAlign: "center",
				textColor: "white"
			}
		},
		{
			name: "scroller1",
			scrollPosition: {
				left: 0,
				top: 0
			},
			type: "Palm.Mojo.Scroller",
			l: 0,
			t: 0,
			h: "100%",
			styles: {
				cursor: "move",
				overflow: "hidden"
			},
			controls: [
				{
					name: "helpContents",
					label: "",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 0,
					h: "100%",
					styles: {
						textColor: "white"
					}
				}
			]
		}
	]
});