opus.Gizmo({
	name: "FirstBW",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	l: 0,
	t: 0,
	h: "100%",
	styles: {
		zIndex: 2,
		bgImage: "",
		bgColor: "black"
	},
	chrome: [
		{
			name: "html1",
			content: "<div class=\"my-scene-fade-top-dark\" x-mojo-scroll-fade=\"top\"></div>",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 51,
			h: "0"
		},
		{
			name: "header1",
			ontap: "showAppMenu",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			r: 0,
			t: 111,
			h: 52,
			hAlign: "left",
			styles: {
				bgColor: "black"
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
			t: 433,
			h: "100%",
			styles: {
				margin: "5",
				cursor: "move",
				overflow: "hidden",
				bgColor: "transparent"
			},
			controls: [
				{
					name: "panel1",
					layoutKind: "hbox",
					dropTarget: true,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 43,
					h: "40",
					controls: [
						{
							name: "picture1",
							ontap: "addABeerTap",
							type: "Palm.Picture",
							l: 0,
							w: 33,
							t: 5,
							h: 37,
							styles: {
								bgImage: "images/plus.png"
							}
						},
						{
							name: "label1",
							ontap: "addABeerTap",
							className: "addBeerButton",
							modelName: "addABeerModel",
							label: "Add a beer",
							type: "Palm.Mojo.Label",
							l: 33,
							r: "",
							w: 272,
							t: 0,
							h: 40,
							styles: {
								padding: "5",
								bold: true,
								borderColor: "",
								borderStyle: "",
								textAlign: "left",
								fontSize: "21px",
								textColor: "white"
							}
						}
					]
				},
				{
					name: "html3",
					content: "<div id=\"scrimSpinner\">\n<div id=\"activity-spinner\" <img src=\"images/hourglass.png\"></img>></div>\n</div>",
					type: "Palm.Mojo.Html",
					l: 0,
					w: "96%",
					t: 39,
					h: "4",
					hAlign: "center",
					styles: {
						bgImage: "images/my-divider.png"
					}
				},
				{
					name: "beerList",
					dropTarget: true,
					onhold: "beerItemHold",
					onholdEnd: "",
					modelName: "beerListModel",
					items: [],
					useSampleData: false,
					title: undefined,
					itemHtml: "<div class=\"palm-row\" id=#{uniqueID} x-mojo-tap-highlight=\"momentary\">\n<div class=\"palm-row-wrapper\">\n   <table width=100%>\n    <tr>\n      <td width=80%>\n        <b>\n        #{beerName}</b><br>\n        <font size=\"2\">\n        #{container} at #{drinkTimeDate}\n        </font>\n      </td>\n      <td width=20%>\n      <div class=\"picture-container\">\n      <div id=\"picture\" class=\"picture-icon\">\n        <img src=\"images/#{stars}stars.png\">\n      </div></div>\n      </td>\n    </tr>\n  </table>\n  </div>\n</div> ",
					dividerTemplateFile: "divider.html",
					onlisttap: "beerItemTap",
					onlistadd: "",
					onlistdelete: "beerItemDelete",
					renderLimit: "20",
					reorderable: false,
					rowTapHighlight: false,
					rowFocusHighlight: false,
					type: "Palm.Mojo.List",
					l: 0,
					b: 65,
					h: 100,
					styles: {
						margin: "5",
						bgColor: "",
						textColor: "white"
					}
				}
			]
		},
		{
			name: "html2",
			content: "<div class=\"my-scene-fade-bottom-dark\" x-mojo-scroll-fade=\"bottom\"></div>",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 252,
			h: "0"
		}
	]
});