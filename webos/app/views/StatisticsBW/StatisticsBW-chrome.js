opus.Gizmo({
	name: "StatisticsBW",
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
			t: 276
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
				overflow: "hidden",
				bgColor: "black"
			},
			controls: [
				{
					name: "statsList",
					dropTarget: true,
					modelName: "statsListModel",
					items: [],
					useSampleData: false,
					title: undefined,
					itemHtml: "<div class=\"palm-row\">\n  <font size=\"3\">\n  #{label}\n  </font>\n  <br>\n  <div align=\"right\">\n  #{value}\n  </div>\n</div>",
					swipeToDelete: false,
					reorderable: false,
					rowTapHighlight: false,
					rowFocusHighlight: false,
					type: "Palm.Mojo.List",
					l: 0,
					w: "95%",
					t: 0,
					h: 100,
					hAlign: "center",
					styles: {
						textColor: "white",
						bgColor: "black"
					}
				}
			]
		}
	]
});