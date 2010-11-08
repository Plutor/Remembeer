opus.Gizmo({
	name: "twitterLogin",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgColor: "",
		bgImage: ""
	},
	chrome: [
		{
			name: "scroller1",
			scrollPosition: {
				left: 0,
				top: 0
			},
			type: "Palm.Mojo.Scroller",
			l: 0,
			t: 0,
			h: 293,
			styles: {
				cursor: "move",
				overflow: "hidden"
			},
			controls: [
				{
					name: "webView",
					modelName: "webViewModel",
					url: "http://twitter.com",
					type: "Palm.Mojo.WebView",
					l: 0,
					t: 0,
					h: 218,
					hAlign: "center",
					styles: {
						border: "2",
						borderColor: "white"
					}
				}
			]
		},
		{
			name: "panel1",
			dropTarget: true,
			type: "Palm.Mojo.Panel",
			l: 0,
			t: 202,
			h: 162,
			styles: {
				bold: true,
				bgColor: "",
				bgImage: "images/remembeer5.jpg",
				textColor: "white"
			},
			controls: [
				{
					name: "label1",
					label: "Log into Twitter above and authorize \"Remembeer\" to  your account.",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 311,
					t: 50,
					h: 48,
					hAlign: "center",
					styles: {
						bold: true,
						textColor: "",
						fontSize: "15px",
						textAlign: "center",
						bgColor: ""
					}
				},
				{
					name: "twitPin",
					modelName: "twitPinModel",
					modifierState: "num-lock",
					hintText: "Enter PIN here",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 97,
					h: "50",
					hAlign: "center",
					styles: {
						border: "1",
						bgColor: "",
						bgImage: "images/50white.png",
						borderColor: "#808080"
					}
				},
				{
					name: "button1",
					ontap: "button1Tap",
					disabled: undefined,
					label: "Authorize",
					type: "Palm.Mojo.Button",
					l: 0,
					w: 158,
					t: 0,
					hAlign: "center"
				}
			]
		}
	]
});