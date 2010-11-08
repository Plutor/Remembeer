opus.Gizmo({
	name: "AboutBW",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgColor: "",
		bgImage: "images/AboutBackground.bmp"
	},
	chrome: [
		{
			name: "header1",
			label: "About Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			t: 24
		},
		{
			name: "panel4",
			layoutKind: "hbox",
			dropTarget: true,
			type: "Palm.Mojo.Panel",
			l: 0,
			t: 50,
			h: 61,
			controls: [
				{
					name: "picture4",
					src: "images/icon48.png",
					type: "Palm.Picture",
					l: 0,
					w: 64,
					t: 2,
					h: 56,
					vAlign: "center"
				},
				{
					name: "panel5",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 67,
					t: 0,
					h: 63,
					controls: [
						{
							name: "label5",
							label: "Remembeer",
							type: "Palm.Mojo.Label",
							l: 0,
							t: 0,
							h: 27,
							styles: {
								margin: "10",
								bold: true,
								textColor: "white"
							}
						},
						{
							name: "version",
							label: "1.0.0 by Xxxx xxx xxxx",
							type: "Palm.Mojo.Label",
							l: 0,
							t: 28,
							h: 37,
							styles: {
								margin: "10",
								textColor: "white",
								fontSize: "16px"
							}
						}
					]
				}
			]
		},
		{
			name: "group2",
			dropTarget: true,
			label: "help",
			type: "Palm.Mojo.Group",
			l: 0,
			t: 159,
			h: 92,
			controls: [
				{
					name: "panel1",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 0,
					h: 51,
					controls: [
						{
							name: "picture1",
							src: "images/globe.png",
							ontap: "discussionTap",
							type: "Palm.Picture",
							l: 0,
							w: 49,
							t: 4,
							h: 47,
							vAlign: "center",
							styles: {
								padding: "5"
							}
						},
						{
							name: "discussion",
							ontap: "discussionTap",
							kind: "title",
							label: "xxxxx",
							type: "Palm.Mojo.Label",
							l: 49,
							w: 251,
							t: 0,
							h: 48,
							styles: {
								margin: "10",
								textColor: "white"
							}
						}
					]
				}
			]
		},
		{
			name: "group3",
			dropTarget: true,
			label: "support",
			type: "Palm.Mojo.Group",
			l: 0,
			t: 206,
			h: 146,
			controls: [
				{
					name: "row5",
					dropTarget: true,
					rowType: "first",
					type: "Palm.Mojo.Row",
					l: 0,
					t: 0,
					controls: [
						{
							name: "panel2",
							layoutKind: "hbox",
							dropTarget: true,
							type: "Palm.Mojo.Panel",
							l: 0,
							t: 0,
							h: 52,
							controls: [
								{
									name: "picture2",
									src: "images/globe.png",
									ontap: "support1Tap",
									type: "Palm.Picture",
									l: 0,
									w: "49",
									t: "4",
									h: "47",
									vAlign: "center",
									styles: {
										padding: "5",
										bgImage: ""
									}
								},
								{
									name: "support",
									ontap: "support1Tap",
									kind: "title",
									label: "xxxxx",
									type: "Palm.Mojo.Label",
									l: 49,
									w: 251,
									t: 0,
									styles: {
										padding: "10",
										textColor: "white"
									}
								}
							]
						}
					]
				},
				{
					name: "row6",
					dropTarget: true,
					rowType: "last",
					type: "Palm.Mojo.Row",
					l: 0,
					t: 52,
					controls: [
						{
							name: "panel3",
							layoutKind: "hbox",
							dropTarget: true,
							type: "Palm.Mojo.Panel",
							l: 0,
							t: 0,
							h: 52,
							controls: [
								{
									name: "picture3",
									src: "images/globe.png",
									ontap: "support2Tap",
									type: "Palm.Picture",
									l: 0,
									w: "49",
									t: "4",
									h: "47",
									styles: {
										padding: "5"
									}
								},
								{
									name: "label2",
									ontap: "support2Tap",
									kind: "title",
									label: "Support Website",
									type: "Palm.Mojo.Label",
									l: 49,
									t: 0,
									styles: {
										padding: "10",
										textColor: "white"
									}
								}
							]
						}
					]
				}
			]
		},
		{
			name: "copyright",
			label: "xxxx",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 349,
			h: 59,
			styles: {
				margin: "10",
				textColor: "white",
				fontSize: "16px"
			}
		},
		{
			name: "panel6",
			layoutKind: "hbox",
			dropTarget: true,
			type: "Palm.Mojo.Panel",
			l: 0,
			t: 0,
			h: "100%",
			controls: [
				{
					name: "panel7",
					layoutKind: "hbox",
					dropTarget: true,
					ontap: "leftTap",
					type: "Palm.Mojo.Panel",
					l: 0,
					w: "40%",
					t: 0,
					h: "100%"
				},
				{
					name: "panel8",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 128,
					w: "20%",
					t: 0,
					h: "100%"
				},
				{
					name: "panel9",
					dropTarget: true,
					ontap: "rightTap",
					type: "Palm.Mojo.Panel",
					l: 192,
					w: "40%",
					t: 0,
					h: "100%"
				}
			]
		}
	]
});