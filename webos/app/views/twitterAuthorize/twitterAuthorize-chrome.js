opus.Gizmo({
	name: "twitterAuthorize",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	h: "100%",
	styles: {
		zIndex: 2,
		bgImage: "images/Remembeer9.bmp"
	},
	chrome: [
		{
			name: "header1",
			label: "Remembeer",
			type: "Palm.Mojo.Header",
			l: 0,
			t: 0
		},
		{
			name: "label1",
			label: "Enter your Twitter user name and password below and then tap \"Submit\"",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 50,
			h: 53,
			styles: {
				fontSize: "15px",
				textAlign: "center"
			}
		},
		{
			name: "twitUser",
			modelName: "twitUserModel",
			hintText: "user name",
			preventResize: true,
			textCase: "cap-lowercase",
			type: "Palm.Mojo.TextField",
			l: "0",
			t: 103,
			hAlign: "center",
			styles: {
				margin: "5",
				border: "1"
			}
		},
		{
			name: "twitPass",
			modelName: "twitPassModel",
			hintText: "password",
			textCase: "cap-lowercase",
			type: "Palm.Mojo.PasswordField",
			l: 0,
			t: 155,
			hAlign: "center",
			styles: {
				margin: "5",
				border: "1"
			}
		},
		{
			name: "button1",
			ontap: "submitTap",
			disabled: undefined,
			label: "Submit",
			type: "Palm.Mojo.Button",
			l: 0,
			w: 161,
			t: 207,
			hAlign: "center"
		},
		{
			name: "html1",
			content: "<div id=\"twitScrim\">\n<div id=\"activity-spinner\" x-mojo-element=\"Spinner\"</div>\n</div>",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 267,
			h: "4"
		}
	]
});