function FbUserDialogAssistant(sceneAssistant,callbackFunc) {
	this.callbackFunc = callbackFunc;
	this.sceneAssistant = sceneAssistant;
	this.controller = sceneAssistant.controller;
}

FbUserDialogAssistant.prototype = {
	setup: function(inDialog) {
		this.widget = inDialog
		Ares.setupDialog(this, inDialog, "fbUserDialog");
	},
	cleanup: function() {
		Ares.cleanupSceneAssistant(this);
	},
	enterButtonTap: function(inSender, event) {
		this.callbackFunc({"name":this.userNameModel.value, "password":this.passwordModel})
		this.widget.mojo.close()
	}
};