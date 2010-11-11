opus.Gizmo({
	name: "Preferences",
	dropTarget: true,
	focusHighlight: false,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgColor: "",
		bgImage: "images/Remembeer7.jpg"
	},
	chrome: [
		{
			name: "header1",
			ontap: "showAppMenu",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			w: 318,
			t: 0
		},
		{
			name: "scroller1",
			scrollPosition: {
				left: 0,
				top: 0
			},
			type: "Palm.Mojo.Scroller",
			l: 0,
			t: 50,
			h: "100%",
			styles: {
				cursor: "move",
				overflow: "hidden"
			},
			controls: [
				{
					name: "label1",
					label: "Basic settings",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "95%",
					t: 0,
					h: 23,
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "",
						fontSize: "15px",
						bgColor: "",
						borderColor: "#808080",
						bgImage: "images/30white.png"
					}
				},
				{
					name: "firstScreen",
					modelName: "firstScreenModel",
					value: 1,
					choices: [
						{
							label: "Beer History",
							value: "1"
						},
						{
							label: "Add a Beer",
							value: "2"
						},
						{
							label: "Statistics",
							value: "3"
						}
					],
					label: "",
					labelPlacement: "left",
					onchange: "firstScreenChange",
					type: "Palm.Mojo.ListSelector",
					l: "0",
					t: 22,
					h: "50",
					hAlign: "center",
					styles: {
						bgImage: "images/chooseback.png",
						textColor: "transparent"
					}
				},
				{
					name: "html1",
					content: "<div id=\"twitScrim\">\n<div id=\"activity-spinner\" x-mojo-element=\"Spinner\"</div>\n</div>\n",
					type: "Palm.Mojo.Html",
					l: 0,
					w: "95%",
					t: 73,
					h: "4",
					hAlign: "center",
					styles: {
						bgImage: "images/my-divider.png"
					}
				},
				{
					name: "nextScreen",
					modelName: "nextScreenModel",
					value: 1,
					choices: [
						{
							label: "Beer History",
							value: "1"
						},
						{
							label: "Add a Beer",
							value: "2"
						},
						{
							label: "Statistics",
							value: "3"
						}
					],
					label: "",
					onchange: "nextScreenChange",
					type: "Palm.Mojo.ListSelector",
					l: 0,
					t: 76,
					h: "50",
					styles: {
						bgImage: "images/nextScreenClearBackground.png",
						bgColor: "",
						textColor: "transparent"
					}
				},
				{
					name: "html2",
					type: "Palm.Mojo.Html",
					l: 0,
					w: "95%",
					t: 130,
					h: "4",
					hAlign: "center",
					styles: {
						bgImage: "images/my-divider.png"
					}
				},
				{
					name: "androidScreens",
					modelName: "androidScreensModel",
					choices: [
						{
							label: "Beers",
							value: "1"
						},
						{
							label: "White on Black",
							value: "2"
						}
					],
					label: "",
					onchange: "androidScreensChange",
					type: "Palm.Mojo.ListSelector",
					l: 0,
					t: 126,
					h: "50",
					styles: {
						textColor: "transparent",
						bgImage: "images/backgroundStyle.png"
					}
				},
				{
					name: "label2",
					label: "Reminder settings",
					type: "Palm.Mojo.Label",
					l: 0,
					r: "",
					w: "95%",
					t: 127,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "",
						fontSize: "15px",
						bgColor: "",
						borderColor: "#808080",
						bgImage: "images/30white.png"
					}
				},
				{
					name: "panel1",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 150,
					h: 56,
					controls: [
						{
							name: "panel9",
							dropTarget: true,
							type: "Palm.Mojo.Panel",
							l: 0,
							t: 0,
							h: 120
						},
						{
							name: "panel11",
							dropTarget: true,
							type: "Palm.Mojo.Panel",
							l: 8,
							w: 264,
							t: 0,
							h: 54,
							controls: [
								{
									name: "label3",
									label: "Show Rating Reminders",
									type: "Palm.Mojo.Label",
									l: 4,
									w: 260,
									t: 0,
									h: 27,
									styles: {
										textColor: "white"
									}
								},
								{
									name: "label10",
									label: "For \"Drink Now\" beers only",
									type: "Palm.Mojo.Label",
									l: 11,
									w: 253,
									t: 27,
									h: 31,
									styles: {
										textColor: "white",
										fontSize: "15px"
									}
								}
							]
						},
						{
							name: "remindersCheck",
							modelName: "remindersCheckModel",
							value: false,
							trueValue: true,
							falseValue: false,
							onchange: "remindersCheckChange",
							type: "Palm.Mojo.CheckBox",
							l: 272,
							w: 48,
							t: 0,
							vAlign: "center",
							styles: {
								textColor: "white"
							}
						}
					]
				},
				{
					name: "html3",
					type: "Palm.Mojo.Html",
					l: 0,
					w: "95%",
					t: 263,
					h: "4",
					hAlign: "center",
					styles: {
						bgImage: "images/my-divider.png"
					}
				},
				{
					name: "panel2",
					showing: false,
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 200,
					h: "50",
					controls: [
						{
							name: "label4",
							showing: false,
							ontap: "",
							label: "Vibrate",
							type: "Palm.Mojo.Label",
							l: 0,
							w: "275",
							t: 0,
							styles: {
								margin: "10",
								textColor: "white"
							}
						},
						{
							name: "vibrateCheck",
							showing: false,
							ontap: "",
							modelName: "vibrateCheckModel",
							trueValue: true,
							falseValue: false,
							onchange: "vibrateCheckChange",
							type: "Palm.Mojo.CheckBox",
							l: 275,
							t: 0,
							vAlign: "center"
						}
					]
				},
				{
					name: "picture2",
					showing: false,
					src: "images/my-divider.png",
					type: "Palm.Picture",
					l: 0,
					w: "95%",
					t: 254,
					h: "4",
					hAlign: "center"
				},
				{
					name: "panel3",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 258,
					h: 59,
					controls: [
						{
							name: "panel4",
							dropTarget: true,
							type: "Palm.Mojo.Panel",
							l: 0,
							w: 253,
							t: 0,
							h: 58,
							controls: [
								{
									name: "label5",
									label: "Ratings Reminder Delay",
									type: "Palm.Mojo.Label",
									l: 6,
									w: 230,
									t: 0,
									h: 32,
									styles: {
										margin: "5",
										textColor: "white"
									}
								},
								{
									name: "label6",
									label: "In Minutes (5-59)",
									type: "Palm.Mojo.Label",
									l: 18,
									w: 218,
									t: 32,
									h: "21",
									styles: {
										textColor: "white",
										fontSize: "15px"
									}
								}
							]
						},
						{
							name: "reminderDelay",
							modelName: "reminderDelayModel",
							value: 5,
							label: "",
							labelPlacement: "right",
							min: "5",
							max: "59",
							onchange: "reminderDelayChange",
							type: "Palm.Mojo.IntegerPicker",
							l: 265,
							w: "100",
							t: 0,
							styles: {
								padding: "0",
								margin: "0",
								bgColor: "",
								textColor: "",
								bgImage: ""
							}
						}
					]
				},
				{
					name: "label7",
					label: "Social settings",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "95%",
					t: 317,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "",
						fontSize: "15px",
						bgColor: "",
						borderColor: "#808080",
						bgImage: "images/30white.png"
					}
				},
				{
					name: "panel5",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 340,
					h: 60,
					controls: [
						{
							name: "panel6",
							dropTarget: true,
							type: "Palm.Mojo.Panel",
							l: 0,
							w: 274,
							t: 0,
							h: "60",
							controls: [
								{
									name: "label8",
									ontap: "",
									label: "Post \"Drink Now\" beers to Twitter",
									type: "Palm.Mojo.Label",
									l: 10,
									w: 259,
									t: 0,
									h: "60",
									styles: {
										textColor: "white"
									}
								}
							]
						},
						{
							name: "twitCheck",
							modelName: "twitCheckModel",
							trueValue: true,
							falseValue: false,
							onchange: "twitPostChange",
							type: "Palm.Mojo.CheckBox",
							l: 248,
							t: 13,
							vAlign: "center"
						}
					]
				},
				{
					name: "picture3",
					showing: false,
					src: "images/my-divider.png",
					type: "Palm.Picture",
					l: 0,
					w: "95%",
					t: 399,
					h: "4",
					hAlign: "center"
				}
			]
		}
	]
});