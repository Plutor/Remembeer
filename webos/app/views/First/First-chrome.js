opus.Gizmo({
	name: "First",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgImage: "images/Remembeer1.jpg"
	},
	chrome: [
		{
			name: "header1",
			ontap: "showAppMenu",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			r: "-4",
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
			t: 433,
			h: "394",
			styles: {
				margin: "5",
				cursor: "move",
				overflow: "hidden"
			},
			controls: [
				{
					name: "panel1",
					layoutKind: "hbox",
					dropTarget: true,
					ontap: "addABeerTap",
					focusHighlight: false,
					type: "Palm.Mojo.Panel",
					l: 0,
					t: 39,
					h: "40",
					controls: [
						{
							name: "picture1",
							src: "images/plus.png",
							ontap: "",
							type: "Palm.Picture",
							l: 0,
							w: 36,
							t: 3,
							h: 34
						},
						{
							name: "label1",
							ontap: "",
							className: "addBeerButton",
							label: "Add a beer",
							type: "Palm.Mojo.Label",
							l: 36,
							r: "",
							w: 271,
							t: 0,
							h: 42,
							styles: {
								margin: "5",
								bold: true,
								borderColor: "",
								borderStyle: "",
								textAlign: "left",
								fontSize: "23px"
							}
						}
					]
				},
				{
					name: "html1",
					content: "<div id=\"scrimSpinner\">\n<!div id=\"activity-spinner\"><!img src=\"images/hourglass.png\"><!/img><!/div>\n<div id=\"activity-spinner\" x-mojo-element=\"Spinner\"</div>\n</div>",
					type: "Palm.Mojo.Html",
					l: 0,
					r: "0",
					w: "96%",
					t: 43,
					h: "4",
					hAlign: "center",
					styles: {
						bgImage: "images/my-divider.png",
						opacity: 1
					}
				},
				{
					name: "beerList",
					dropTarget: true,
					onhold: "beerItemHold",
					modelName: "beerListModel",
					items: [],
					useSampleData: false,
					title: undefined,
					itemHtml: "<div class=\"palm-row\" id=#{uniqueID} x-mojo-tap-highlight=\"momentary\">\n<div class=\"palm-row-wrapper\">\n   <table width=100%>\n    <tr>\n      <td width=80%>\n        <b>\n        #{beerName}</b><br>\n        <font size=\"2\">\n        #{container} at #{drinkTimeDate}\n        </font>\n      </td>\n      <td width=20%>\n      <div class=\"picture-container\">\n      <div id=\"picture\" class=\"picture-icon\">\n        <img src=\"images/#{stars}stars.png\">\n      </div></div>\n      </td>\n    </tr>\n  </table>\n  </div>\n</div> \n",
					dividerTemplateFile: "../../divider",
					onlisttap: "beerItemTap",
					onlistadd: "",
					onlistdelete: "beerItemDelete",
					reorderable: false,
					type: "Palm.Mojo.List",
					l: 0,
					t: 0,
					h: 100,
					styles: {
						margin: "5",
						bgColor: ""
					}
				}
			]
		},
		{
			name: "html2",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 261,
			h: 21
		}
	]
});