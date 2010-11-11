opus.Gizmo({
	name: "dashboard",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	t: 0,
	h: 71,
	styles: {
		zIndex: 2
	},
	chrome: [
		{
			name: "panel1",
			layoutKind: "hbox",
			dropTarget: true,
			type: "Palm.Mojo.Panel",
			l: 0,
			t: 0,
			h: 60,
			controls: [
				{
					name: "html1",
					content: "<div class=\"dashboard-notification-module\">\n    <div class=\"palm-dashboard-icon-container\">\n      <img src=\"images/icon40.png\"/>\n    </div>\n    <div class=\"palm-dashboard-text-container\">\n         <div class=\"dashboard-title\">\n            <font align=\"center\">\n            Remembeer Rating Reminder\n            </font>\n         </div>\n        <div id='dashboard-text' class=\"palm-dashboard-text\">\n            #{text}</div>\n    </div>\n</div>\n      <!/table>\n    </div>",
					ontap: "dashboardTap",
					type: "Palm.Mojo.Html",
					l: 0,
					t: 0
				}
			]
		}
	]
});