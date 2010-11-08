function ImportExportDialogAssistant(sceneAssistant,callbackFunc) {
	this.callbackFunc = callbackFunc;
	this.sceneAssistant = sceneAssistant;
	this.controller = sceneAssistant.controller;
}

ImportExportDialogAssistant.prototype = {
	setup: function(inDialog) {
		this.widget = inDialog
		Ares.setupDialog(this, inDialog, "importExportDialog");
		this.importButtonTap = this.importButtonTap.bind(this)
	},
	cleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	expToEmailTap: function(inSender, event) {
		this.callbackFunc(0)
		this.widget.mojo.close()
	},
	expLocallyTap: function(inSender, event) {
		this.callbackFunc(1)
		this.widget.mojo.close()
	},
	importButtonTap: function(inSender, event) {
		this.callbackFunc(2)
		this.widget.mojo.close()
	}
};