myPopupBase = Class.create( {
	sceneSetup: function() {
		Ares.setupSceneAssistant(this);
		//while( (stageController=Mojo.Controller.getAppController().getStageController('First'))==undefined){}
		//stageController.setWindowOrientation("up");
		this.vibrate = this.vibrate.bind(this)
		this.appController = Mojo.Controller.getAppController();
		this.buttonTapped = undefined
		this.vibrate(true)
	},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this);
		Mojo.Log.info('in popup cleanup: '+this.buttonTapped)
		var dashboardStage = this.appController.getStageProxy("myDashboard")
		if( this.buttonTapped != undefined ){
			if (dashboardStage)
				dashboardStage.delegateToSceneAssistant("updateDashboard", this.buttonTapped);
		}
		else{
			if (dashboardStage)
				dashboardStage.delegateToSceneAssistant("updateDashboard", "laterPressed");
		}
	},
	vibrate: function(yesNo){
		if( yesNo == true ){
			Mojo.Controller.getAppController().playSoundNotification("vibrate", "")
			setTimeout( function(){ Mojo.Controller.getAppController().playSoundNotification('vibrate', '', 1000); }, 1000 );
			setTimeout( function(){ Mojo.Controller.getAppController().playSoundNotification('vibrate', '', 1000); }, 2000 );
			setTimeout( function(){ Mojo.Controller.getAppController().playSoundNotification('vibrate', '', 1000); }, 3000 );
			setTimeout( function(){ Mojo.Controller.getAppController().playSoundNotification('vibrate', '', 1000); }, 4000 );
		}
	},
	okButtonTap: function(inSender, event) {
		this.buttonTapped = 'okPressed'
		this.controller.window.close()
	},
	laterButtonTap: function(inSender, event) {
		this.buttonTapped = 'laterPressed'
		this.controller.window.close()
	},
	dismissButtonTap: function(inSender, event) {
		Mojo.Controller.getAppController().closeStage("myDashboard")
		this.controller.window.close()
	}
})