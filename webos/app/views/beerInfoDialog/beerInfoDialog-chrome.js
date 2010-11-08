opus.Gizmo({
	name: "beerInfoDialog",
	dropTarget: true,
	type: "Palm.Mojo.Panel",
	t: 0,
	h: 177,
	styles: {
		zIndex: 2,
		bgColor: ""
	},
	chrome: [
		{
			name: "button1",
			ontap: "drinkAnotherTap",
			disabled: undefined,
			label: "Drink Another",
			type: "Palm.Mojo.Button",
			l: 0,
			w: 245,
			t: 0,
			hAlign: "center"
		},
		{
			name: "button2",
			ontap: "editBeerInfoTap",
			disabled: undefined,
			label: "Edit Beer Info",
			type: "Palm.Mojo.Button",
			l: 0,
			w: "245",
			t: 59,
			hAlign: "center"
		},
		{
			name: "label1",
			label: "(Swipe item in beer list to delete)",
			type: "Palm.Mojo.Label",
			l: 0,
			t: 120,
			h: 24,
			styles: {
				fontSize: "15px",
				textAlign: "center",
				textColor: ""
			}
		}
	]
});