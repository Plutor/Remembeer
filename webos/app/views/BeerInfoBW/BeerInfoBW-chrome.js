opus.Gizmo({
	name: "BeerInfoBW",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgColor: "black"
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
				bgColor: "#202020"
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
			t: 99,
			styles: {
				padding: "10",
				textColor: "white",
				bgColor: "#202020",
				oneLine: true
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
			t: 452,
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
					t: 46,
					h: 23,
					styles: {
						fontSize: "15px",
						textColor: "white"
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
					t: 45,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px"
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
						textColor: "black",
						bgColor: "white"
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
					t: 241,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px",
						bgColor: "#202020"
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
						bgColor: "white"
					}
				},
				{
					name: "label7",
					label: "Location",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 241,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px",
						bgColor: "#202020"
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
						bgColor: "white",
						textColor: "black"
					}
				},
				{
					name: "label6",
					label: "Style",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 241,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px",
						bgColor: "#202020"
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
						bgColor: "white"
					}
				},
				{
					name: "label5",
					label: "ABV",
					type: "Palm.Mojo.Label",
					l: 0,
					t: 241,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px",
						bgColor: "#202020"
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
						bgColor: "#202020"
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
								bgColor: "white",
								textColor: "black"
							}
						},
						{
							name: "label4",
							label: "%",
							type: "Palm.Mojo.Label",
							l: 96,
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
					t: 521,
					h: "23",
					styles: {
						textColor: "white",
						fontSize: "15px",
						bgColor: "#202020"
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
						textColor: "black",
						bgColor: "white"
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
						bgColor: "#202020"
					},
					controls: [
						{
							name: "button1",
							ontap: "saveInfoTap",
							disabled: undefined,
							label: "Save info",
							type: "Palm.Mojo.Button",
							l: 90,
							w: 139,
							t: 0,
							hAlign: "center"
						}
					]
				}
			]
		}
	]
});