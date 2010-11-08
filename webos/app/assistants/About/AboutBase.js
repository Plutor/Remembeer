AboutBase = Class.create( {
	sceneSetup: function() {
		Ares.setupSceneAssistant(this);
		this.appMenuModel = {
			visible: true,
			items: [
				{ label: "Close this menu", command: 'palm-show-app-menu'}]
		};
		this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, this.appMenuModel);
	},
	sceneCleanup: function() {
		Ares.cleanupSceneAssistant(this)
	},
	sceneAboutToActivate: function(callback) {
		this.controller.get('version').update(Mojo.Controller.appInfo.version+' by '+Mojo.Controller.appInfo.vendor)
		this.controller.get('copyright').update(Mojo.Controller.appInfo.copyright)
		this.controller.get('discussion').update(Mojo.Controller.appInfo.support.resources[0].label)
		this.controller.get('support').update(Mojo.Controller.appInfo.vendor)
		callback()
	},
	support2Tap: function(inSender, event) {
		this.controller.serviceRequest("palm://com.palm.applicationManager", {
			method: "open",
			parameters:  {
				id: 'com.palm.app.browser',
				params: {
					target: Mojo.Controller.appInfo.vendorurl
				}
			}
		}) 
	},
	support1Tap: function(inSender, event) {
		this.controller.serviceRequest("palm://com.palm.applicationManager", {
			method: "open",
			parameters:  {
				id: 'com.palm.app.browser',
				params: {
					target: Mojo.Controller.appInfo.support.url
				}
			}
		}) 
	},
	discussionTap: function(inSender, event) {
		this.controller.serviceRequest("palm://com.palm.applicationManager", {
			method: "open",
			parameters:  {
				id: 'com.palm.app.browser',
				params: {
					target: Mojo.Controller.appInfo.support.resources[0].url
				}
			}
		})  
	}
})