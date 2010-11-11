opus.Gizmo({
	name: "AddBeerBW",
	dropTarget: true,
	onhold: "holdForList",
	onholdEnd: "",
	focusHighlight: false,
	type: "Palm.Mojo.Panel",
	l: 0,
	w: 320,
	t: "",
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
					t: 0,
					h: 23,
					styles: {
						textColor: "white",
						fontSize: "15px"
					}
				},
				{
					name: "beerName",
					onhold: "",
					modelName: "beerNameModel",
					autoFocus: true,
					hintText: "...hold for Beer List",
					textCase: "cap-title",
					onchange: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 23,
					h: "45",
					styles: {
						padding: "10",
						bgColor: "white",
						opacity: 1,
						borderStyle: "",
						textColor: "black"
					}
				},
				{
					name: "label2",
					label: "Container:",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 68,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px"
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
					w: "95%",
					t: 91,
					h: "40",
					hAlign: "center",
					styles: {
						padding: "5",
						bgColor: "#202020",
						oneLine: false,
						bgImage: "images/listselectorback.bmp",
						textColor: "white"
					}
				},
				{
					name: "label3",
					label: "Drink When:",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 131,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px"
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
					w: "95%",
					t: 154,
					h: "40",
					hAlign: "center",
					vAlign: "center",
					styles: {
						padding: "5",
						bgColor: "#202020",
						textColor: "white",
						bgImage: "images/listselectorback.bmp"
					}
				},
				{
					name: "label4",
					label: "Notes:",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 194,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px"
					}
				},
				{
					name: "beerNote",
					modelName: "beerNoteModel",
					multiline: true,
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 217,
					styles: {
						bgColor: "white"
					}
				},
				{
					name: "doneButton",
					ontap: "doneButtonTap",
					disabled: undefined,
					label: "Save Beer!",
					type: "Palm.Mojo.Button",
					l: 0,
					w: 136,
					t: 269,
					b: -29,
					hAlign: "center"
				}
			]
		},
		{
			name: "html1",
			content: "<div id=\"scrimSpinner\">\n<div id=\"activity-spinner\" x-mojo-element=\"Spinner\"</div>\n</div>",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 389,
			h: "4"
		}
	]
});