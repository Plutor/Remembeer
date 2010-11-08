function PickTimeDateAssistant(sceneAssistant,callbackFunc) {
	this.callbackFunc = callbackFunc;
	this.sceneAssistant = sceneAssistant;
	this.controller = sceneAssistant.controller;
}

PickTimeDateAssistant.prototype = {
	setup: function(inDialog) {
		this.widget = inDialog
		Ares.setupDialog(this, inDialog, "pickTimeDate");
	},
	activate: function() {
		var d = new Date()
		//Mojo.Log.info('activating; '+d)
		this.timeModel.time = d
		this.controller.modelChanged(this.timeModel)
		this.dateModel.date = d
		this.controller.modelChanged(this.dateModel)
		//Mojo.Log.info(this.timeModel.time)
	},
	cleanup: function() {
		Ares.cleanupSceneAssistant(this)
		this.callbackFunc(this.timeDateString)
	},
	okButtonTap: function(inSender, event) {
		hour = this.timeModel.time.getHours()
		if( hour < 10 ) hour = '0'+hour
		min = this.timeModel.time.getMinutes()
		if( min < 10 ) min = '0'+min
		sec = this.timeModel.time.getSeconds()
		if( sec < 10 ) sec = '0'+sec
		day = this.dateModel.date.getDate()
		if( day < 10 ) day = '0'+day
		month = this.dateModel.date.getMonth()+1
		if( month < 10 ) month = '0'+month
		year = this.dateModel.date.getFullYear()
		this.timeDateString = this.dateModel.date.getFullYear()+'-'+month+'-'+day+' '+hour+':'+min+':'+sec
		this.widget.mojo.close()
	}
};