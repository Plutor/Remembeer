opus.Gizmo({
	name: "AddBeer",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	t: "",
	b: "0",
	h: "100%",
	styles: {
		zIndex: 2,
		bgImage: "images/Remembeer4.jpg"
	},
	chrome: [
		{
			name: "header1",
			ontap: "showAppMenu",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			t: 0,
			styles: {
				bgColor: "",
				opacity: 1,
				textAlign: "center",
				textColor: ""
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
					name: "label1",
					label: "Beer Name:",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 88,
					t: 0,
					h: 21,
					styles: {
						fontSize: "15px",
						bgColor: ""
					}
				},
				{
					name: "beerName",
					ontap: "",
					onhold: "",
					modelName: "beerNameModel",
					autoFocus: true,
					hintText: "...hold for Beer List",
					textCase: "cap-title",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 21,
					h: "45",
					styles: {
						padding: "10",
						border: "1",
						bgColor: "",
						opacity: 1,
						borderStyle: "",
						textColor: "black",
						bgImage: "images/50white.png",
						borderColor: "#808080"
					}
				},
				{
					name: "label2",
					label: "Container:",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 76,
					t: 66,
					h: 22,
					styles: {
						fontSize: "15px",
						bgColor: "#fbca3f"
					}
				},
				{
					name: "containerList",
					modelName: "containerModel",
					value: 0,
					choices: [
						{
							label: "Bottle",
							value: "0"
						},
						{
							label: "Draught",
							value: "1"
						},
						{
							label: "Can",
							value: "2"
						},
						{
							label: "Growler",
							value: "3"
						}
					],
					label: "",
					type: "Palm.Mojo.ListSelector",
					l: 0,
					t: 88,
					h: "40",
					styles: {
						padding: "7",
						border: "1",
						bgColor: "",
						bgImage: "images/30white.png",
						borderColor: "#808080"
					}
				},
				{
					name: "label3",
					label: "Drink When:",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "89",
					t: 128,
					h: 23,
					styles: {
						fontSize: "15px",
						bgColor: "#fbca3f"
					}
				},
				{
					name: "drinkWhenList",
					modelName: "drinkWhenModel",
					value: 1,
					choices: [
						{
							label: "Drink Now",
							value: "1"
						},
						{
							label: "Last Night",
							value: "2"
						},
						{
							label: "Ten Minutes Ago",
							value: "3"
						},
						{
							label: "Specific Time",
							value: "4"
						}
					],
					label: "",
					onchange: "drinkWhenChange",
					type: "Palm.Mojo.ListSelector",
					l: 0,
					t: 151,
					h: "40",
					styles: {
						padding: "7",
						border: "1",
						bgColor: "",
						bgImage: "images/30white.png",
						borderColor: "#808080"
					}
				},
				{
					name: "label4",
					label: "Notes:",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 54,
					t: 191,
					h: "23",
					styles: {
						fontSize: "15px",
						bgColor: "#fbca3f"
					}
				},
				{
					name: "textField1",
					modelName: "beerNoteModel",
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 214,
					h: "50",
					styles: {
						border: "1",
						bgImage: "images/30white.png",
						borderColor: "#808080"
					}
				},
				{
					name: "doneButton",
					ontap: "doneButtonTap",
					disabled: undefined,
					label: "Save Beer!",
					type: "Palm.Mojo.Button",
					l: 0,
					w: 137,
					t: 264,
					b: -4,
					hAlign: "center"
				}
			]
		},
		{
			name: "html1",
			content: "<div id=\"scrimSpinner\">\n<div id=\"activity-spinner\" x-mojo-element=\"Spinner\"</div>\n</div>",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 374,
			h: "4"
		}
	]
});