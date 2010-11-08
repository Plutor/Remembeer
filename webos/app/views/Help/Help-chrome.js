opus.Gizmo({
	name: "Help",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgImage: "images/Remembeer13.jpg"
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
			label: "",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 50,
			h: 59,
			styles: {
				margin: "20",
				textColor: "white",
				textAlign: "center"
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