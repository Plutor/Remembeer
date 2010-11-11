opus.Gizmo({
	name: "BeerInfo",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgColor: "",
		bgImage: "images/Remembeer12.jpg"
	},
	chrome: [
		{
			name: "header1",
			ontap: "showAppMenu",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			t: 49,
			styles: {
				bgColor: ""
			}
		},
		{
			name: "html1",
			content: "<div id=\"scrimSpinner\">\n<div id=\"activity-spinner\" x-mojo-element=\"Spinner\"</div>\n</div>",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 49,
			h: "4"
		},
		{
			name: "beerInfoHeader",
			modelName: "beerInfoHeaderModel",
			label: "",
			type: "Palm.Mojo.Label",
			l: 0,
			w: 310,
			t: 99,
			styles: {
				padding: "10",
				textColor: "black",
				bgColor: "",
				oneLine: true,
				opacity: "",
				borderColor: "",
				bgImage: ""
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
			t: 99,
			h: "100%",
			styles: {
				cursor: "move",
				overflow: "hidden"
			},
			controls: [
				{
					name: "label2",
					label: "Your rating:",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "98%",
					t: 46,
					h: 23,
					hAlign: "center",
					styles: {
						border: "1",
						fontSize: "15px",
						textColor: "white",
						bgImage: "",
						borderColor: "#808080"
					}
				},
				{
					name: "ratingPanel",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 22,
					h: "40",
					controls: [
						{
							name: "label10",
							ontap: "beerStar0Tap",
							label: "",
							type: "Palm.Mojo.Label",
							l: 0,
							w: "12%",
							t: 0,
							h: "40"
						},
						{
							name: "beerStar1",
							content: "<img src=\"images/#{star}.png\">\n\n",
							ontap: "beerStar1Tap",
							modelName: "beerStar1Model",
							type: "Palm.Mojo.Html",
							l: 77,
							w: "15%",
							t: 0,
							h: "40"
						},
						{
							name: "beerStar2",
							content: "<img src=\"images/#{star}.png\">",
							ontap: "beerStar2Tap",
							modelName: "beerStar2Model",
							type: "Palm.Mojo.Html",
							l: 124,
							w: "15%",
							t: 0,
							h: "40"
						},
						{
							name: "beerStar3",
							content: "<img src=\"images/#{star}.png\">",
							ontap: "beerStar3Tap",
							modelName: "beerStar3Model",
							type: "Palm.Mojo.Html",
							l: 124,
							w: "15%",
							t: 0,
							h: "40"
						},
						{
							name: "beerStar4",
							content: "<img src=\"images/#{star}.png\">",
							ontap: "beerStar4Tap",
							modelName: "beerStar4Model",
							type: "Palm.Mojo.Html",
							l: 125,
							w: "15%",
							t: 0,
							h: "40"
						},
						{
							name: "beerStar5",
							content: "<img src=\"images/#{star}.png\">",
							ontap: "beerStar5Tap",
							modelName: "beerStar5Model",
							type: "Palm.Mojo.Html",
							l: 229,
							w: "15%",
							t: 0,
							h: "40"
						}
					]
				},
				{
					name: "label3",
					label: "Notes:",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "98%",
					t: 45,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "white",
						fontSize: "15px",
						bgImage: "",
						borderColor: "#808080"
					}
				},
				{
					name: "beerNote",
					modelName: "beerNoteModel",
					autoHeight: false,
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 74,
					styles: {
						textColor: "white",
						bgColor: "#202020",
						opacity: "",
						borderColor: "",
						bgImage: ""
					}
				},
				{
					name: "saveRating",
					ontap: "saveRatingTap",
					disabled: undefined,
					label: "Save Rating",
					type: "Palm.Mojo.Button",
					l: 0,
					w: 160,
					t: 112,
					hAlign: "center"
				},
				{
					name: "label8",
					label: "Brewery",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "98%",
					t: 241,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "white",
						fontSize: "15px",
						bgColor: "",
						opacity: "",
						bgImage: "",
						borderColor: "#808080"
					}
				},
				{
					name: "breweryName",
					modelName: "breweryNameModel",
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 241,
					styles: {
						padding: "5",
						bgColor: "#202020",
						opacity: "",
						borderColor: "white",
						bgImage: "",
						textColor: "white"
					}
				},
				{
					name: "label7",
					label: "Location",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "98%",
					t: 241,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "white",
						fontSize: "15px",
						bgColor: "",
						bgImage: "",
						borderColor: "#808080"
					}
				},
				{
					name: "breweryLocation",
					modelName: "locationModel",
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 241,
					styles: {
						padding: "5",
						bgColor: "#202020",
						textColor: "white",
						opacity: "",
						borderColor: "",
						bgImage: ""
					}
				},
				{
					name: "label6",
					label: "Style",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "98%",
					t: 241,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "white",
						fontSize: "15px",
						bgColor: "",
						bgImage: "",
						borderColor: "#808080"
					}
				},
				{
					name: "beerStyle",
					modelName: "styleModel",
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 241,
					styles: {
						padding: "5",
						bgColor: "#202020",
						opacity: "",
						borderColor: "",
						bgImage: "",
						textColor: "white"
					}
				},
				{
					name: "label5",
					label: "ABV",
					type: "Palm.Mojo.Label",
					l: 0,
					w: 126,
					t: 241,
					h: "23",
					hAlign: "left",
					styles: {
						border: "1",
						textColor: "white",
						fontSize: "15px",
						bgColor: "",
						borderColor: "#808080",
						bgImage: ""
					}
				},
				{
					name: "panel1",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 241,
					h: "52",
					styles: {
						bgColor: ""
					},
					controls: [
						{
							name: "ABV",
							modelName: "ABVModel",
							modifierState: "num-lock",
							hintText: "",
							type: "Palm.Mojo.TextField",
							l: 0,
							w: "30%",
							t: 0,
							styles: {
								padding: "5",
								bgColor: "#202020",
								textColor: "white",
								opacity: "",
								borderColor: "",
								bgImage: ""
							}
						},
						{
							name: "label4",
							label: "%",
							type: "Palm.Mojo.Label",
							l: 45,
							w: "70%",
							t: 0,
							h: "52",
							styles: {
								padding: "10",
								textColor: "white"
							}
						}
					]
				},
				{
					name: "label1",
					label: "About this beer",
					type: "Palm.Mojo.Label",
					l: 0,
					w: "98%",
					t: 521,
					h: "23",
					hAlign: "center",
					styles: {
						border: "1",
						textColor: "white",
						fontSize: "15px",
						bgColor: "",
						borderColor: "#808080",
						bgImage: ""
					}
				},
				{
					name: "aboutBeer",
					modelName: "aboutModel",
					hintText: "",
					type: "Palm.Mojo.TextField",
					l: 0,
					t: 544,
					styles: {
						textColor: "white",
						bgColor: "#202020",
						opacity: "",
						borderColor: "white",
						bgImage: ""
					}
				},
				{
					name: "panel3",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 597,
					h: 85,
					styles: {
						bgColor: ""
					},
					controls: [
						{
							name: "button1",
							ontap: "saveInfoTap",
							disabled: undefined,
							label: "Save Info",
							type: "Palm.Mojo.Button",
							l: 94,
							w: 132,
							t: 0,
							hAlign: "center"
						}
					]
				}
			]
		}
	]
});