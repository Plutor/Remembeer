function DashboardAssistant(argFromPusher) {
	this.message = argFromPusher.message
	this.delay = argFromPusher.delay
	this.beerID = argFromPusher.beerID
	this.beerName = argFromPusher.beerName
}

DashboardAssistant.prototype = {
	setup: function() {
		Ares.setupSceneAssistant(this);
		this.activateHandler=this.activateWindow.bind(this)
		Mojo.Event.listen(this.controller.stageController.document, Mojo.Event.stageActivate, this.activateHandler);
		this.deactivateHandler=this.deactivateWindow.bind(this);
		Mojo.Event.listen(this.controller.stageController.document, Mojo.Event.stageDeactivate, this.deactivateHandler);
		var props = {
			text: this.beerName
		};
		var messageText = Mojo.View.render({object: props, template: 'dashboard/text-template'});
		var messageDiv = this.controller.get('dashboard-text');
		Element.update(messageDiv, messageText);
		this.waitForTimeout = this.waitForTimeout.bind(this)
		this.updateDashboard = this.updateDashboard.bind(this)
		this.waitForTimeout()
	},
	cleanup: function() {
		Ares.cleanupSceneAssistant(this);
		Mojo.Event.stopListening(this.controller.stageController.document, Mojo.Event.stageActivate, this.activateHandler);
		Mojo.Event.stopListening(this.controller.stageController.document, Mojo.Event.stageDeactivate, this.deactivateHandler);
		Mojo.Log.info('in cleanup: '+this.dashMessage)
		this.schedulerSetRequest = new Mojo.Service.Request(
			"palm://com.palm.power/timeout", {
				method: "clear",
				parameters: {"key": "remembeer"},
				onSuccess: function(event){},
				onFailure: function(event){}
			}
		)
	},
	waitForTimeout: function(){
			this.timerStarted = new Date()
			Mojo.Controller.getAppController().showBanner(this.message,'')
			this.controller.serviceRequest("palm://com.palm.power/timeout", {
				method: "set",
				parameters: {
					"wakeup": true,
					"key": "remembeer",
					"uri": "palm://com.palm.applicationManager/launch",
					"params": '{"id":"com.gelbintergalactic.remembeer","params":{"action":"timeout"}}',
					"in": this.delay
				},
				onSuccess: function(event){Mojo.Log.info('timer success')},
				onFailure: function(event){Mojo.Log.info('timer failure: '+Object.toJSON(event))}
			})
	},
	updateDashboard: function(message){
		if( message == 'okPressed' ){
			this.controller.serviceRequest('palm://com.palm.applicationManager', {
				method: 'launch',
				parameters: {
					id: 'com.gelbintergalactic.remembeer',
					params: {"action":"beerInfo", "beerID":this.beerID}
				}
			})
		}
		else if( message == 'laterPressed' ) {
			this.message = 'Continue to wait'
			this.waitForTimeout()
		}
		else {
			Mojo.Controller.getAppController
		}
	},
	dashboardTap: function(inSender, event) {
		this.schedulerSetRequest = new Mojo.Service.Request(
			"palm://com.palm.power/timeout", {
				method: "clear",
				parameters: {"key": "remembeer"},
				onSuccess: function(event){Mojo.Log.info('timer success')},
				onFailure: function(event){Mojo.Log.info('timer failure: '+Object.toJSON(event))}
			}
		)
		this.updateDashboard("okPressed")
	},
	activateWindow: function(event) {
		var now = new Date()
		var passed = now - this.timerStarted
		Mojo.Log.info('passed: '+passed)
		var stg = this.delay.slice(3,5)
		Mojo.Log.info('stg: '+stg)
		var toGo = (stg*60000) - passed
		Mojo.Log.info('toGo: '+toGo)
		var mins = Math.floor(toGo/60000)
		if( mins < 10 ) mins = '0'+mins
		Mojo.Log.info('mins: '+mins)
		var secs = Math.floor((toGo-(mins*60000))/1000)
		if( secs < 10 ) secs = '0'+secs
		Mojo.Log.info('secs: '+secs)
		var props = {
			text: '00:'+mins+':'+secs
		};
		var messageText = Mojo.View.render({object: props, template: 'dashboard/text-template'});
		var messageDiv = this.controller.get('dashboard-text');
		//Element.update(messageDiv, messageText);
	},
	deactivateWindow: function(event) {
		
	}
};