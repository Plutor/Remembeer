HelpBase = Class.create( {
	sceneSetup: function(BW) {
		Ares.setupSceneAssistant(this);
		this.BW = BW
		this.appMenuModel = {
			visible: true,
			items: [
				{ label: "Close this menu", command: 'palm-show-app-menu'},
				{ label: "About", command: 'do-myAbout' }]
		};
		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
		this.gotHelpFile = this.gotHelpFile.bind(this)
	},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	sceneAboutToActivate: function(callback) {
		this.readyToGo = callback
		// Read the context help file
		var request = new Ajax.Request('ContextHelp.html', {
			method: 'get',
			onSuccess: this.gotHelpFile,
			onFailure: function(){}
		});
	},
	handleCommand: function(event) {
		if(event.type == Mojo.Event.command) {
			if(event.command == 'do-myAbout') {
				if( this.BW == "BW" ) nextScreen = 'AboutBW'
				else nextScreen = 'About'
				//this.controller.stageController.pushAppSupportInfoScene()
				this.controller.stageController.pushScene({'name':nextScreen,transition: Mojo.Transition.crossFade})
			}
		}
	},
	gotHelpFile: function(results) {
		helpText = new String(results.responseText)
		find = helpText.indexOf('<!'+this.context)
		body = helpText.indexOf('<body>',find)
		end = helpText.indexOf('</body>',body)
		text = helpText.slice(body+7,end-1)
		this.controller.get('heading').innerHTML = '<b>'+this.context+' Help</b>'
		this.controller.get('helpContents').innerHTML = text
		this.readyToGo()
	},
	showAppMenu: function(inSender, event) {
		Mojo.Log.info('in showAppMenu')
		Mojo.Controller.stageController.sendEventToCommanders(Mojo.Event.make(Mojo.Event.command, {command: "palm-show-app-menu"}))
	}
})