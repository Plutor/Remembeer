opus.Gizmo({
	name: "fbUserDialog",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	t: 0,
	h: 262,
	styles: {
		zIndex: 2
	},
	chrome: [
		{
			name: "label1",
			label: "Enter your Facebook information and then press the \"Enter\" button.",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 0,
			h: 61,
			styles: {
				fontSize: "18px",
				textAlign: "center"
			}
		},
		{
			name: "userName",
			modelName: "userNameModel",
			hintText: "Facebook user name",
			type: "Palm.Mojo.TextField",
			l: 0,
			w: "95%",
			t: 0,
			hAlign: "center",
			styles: {
				padding: "10",
				border: "1"
			}
		},
		{
			name: "html1",
			type: "Palm.Mojo.Html",
			l: 0,
			t: 113,
			h: 9
		},
		{
			name: "password",
			type: "Palm.Mojo.PasswordField",
			l: 0,
			w: "95%",
			t: 121,
			hAlign: "center",
			styles: {
				padding: "10",
				border: "1"
			}
		},
		{
			name: "enterButton",
			ontap: "enterButtonTap",
			disabled: undefined,
			label: "Enter",
			type: "Palm.Mojo.Button",
			l: 0,
			w: 117,
			t: 113,
			hAlign: "center"
		}
	]
});