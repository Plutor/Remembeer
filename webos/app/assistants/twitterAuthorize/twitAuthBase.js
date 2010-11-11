TwitAuthBase = Class.create( {
	sceneSetup: function(BW){
		this.BW = BW
		Ares.setupSceneAssistant(this)
		this.makeTimeStamp = this.makeTimeStamp.bind(this)
		this.makeNonce = this.makeNonce.bind(this)
		this.gotAuthToken = this.gotAuthToken.bind(this)
		this.authTokenFailure = this.authTokenFailure.bind(this)
		this.twitSpinner = this.controller.get("activity-spinner")
		this.controller.setupWidget("activity-spinner",{'spinnerSize':Mojo.Widget.spinnerLarge},this.spinnerModel={'spinning':true})
		this.scrim = Mojo.View.createScrim(this.controller.document, {scrimClass:'palm-scrim'})
		this.scrim.hide()
		this.controller.get("twitScrim").appendChild(this.scrim).appendChild(this.controller.get(this.twitSpinner));
		cookie = new Mojo.Model.Cookie("twitParms")
		twitParms = cookie.get()
		if( twitParms != undefined ){
			this.twitUserModel.value = twitParms.twitUser
			this.controller.modelChanged(this.twitUserModel)
			this.twitPassModel.value = twitParms.twitPass
			this.controller.modelChanged(this.twitPassModel)
		}
	},
	sceneCleanup: function(){
		Ares.cleanupSceneAssistant(this);
	},
	submitTap: function(inSender, event) {
		this.userName = this.twitUserModel.value
		this.password = this.twitPassModel.value
		if( this.userName == '' || this.password == '' )
			this.controller.showAlertDialog( {
				title:"Entry Error",
				message:"You must provide both user name and password",
				onChoose: this.twitAuthorize,
				choices: [ {label:"OK", value:"OK"} ]
			})
		else{
			this.scrim.show()
			this.consumerSecret = 'VyKlwVckeFps6befs6kRHa4YGrbDHMfknq14VvwWo'
			this.consumerKey = 'D3iN6bOTkA7SF1oAl1wgrA'
			this.accessor = { consumerSecret: this.consumerSecret }
			this.message = { method: 'POST', action: 'https://api.twitter.com/oauth/auth_token', 
								parameters: [["oauth_version", "1.0"], ["oauth_consumer_key", this.consumerKey],
															["oauth_signature_method", "HMAC-SHA1" ]]}
			this.message.action = 'https://api.twitter.com/oauth/access_token'
			this.accessor.tokenSecret = this.tokenSecret
			this.message.parameters = []
			this.message.parameters.push([ "oauth_version", "1.0"])
			this.message.parameters.push([ "oauth_consumer_key", this.consumerKey])
			this.message.parameters.push([ "oauth_signature_method", "HMAC-SHA1" ])
			this.message.parameters.push([ "oauth_token", this.requestToken ])
			this.message.parameters.push([ "oauth_timestamp", this.makeTimeStamp() ])
			this.message.parameters.push([ "oauth_nonce", this.makeNonce() ])
			this.message.parameters.push([ "oauth_verifier", this.twitPin ])
			this.message.parameters.push([ "x_auth_mode", "client_auth" ])
			this.message.parameters.push([ "x_auth_username", this.userName ])
			this.message.parameters.push([ "x_auth_password", this.password ])
			OAuth.SignatureMethod.sign(this.message, this.accessor);
			datasend = OAuth.formEncode(this.message.parameters);
			var myAjax = new Ajax.Request(this.message.action, {
			 	method: 'POST',
			 	postBody: datasend,
			 	onComplete: this.gotAuthToken,
			 	onFailure: this.authTokenFailure
			})
		}
	},
	gotAuthToken: function(transport){
		if (transport.status == 200) {
			Mojo.Log.info(transport.responseText)
			parms = transport.responseText.split('=')
			this.twitToken = parms[1].split('&')[0]
			this.twitSecret = parms[2].split('&')[0]
			cookie = new Mojo.Model.Cookie("twitParms")
			cookie.put({twitUser:this.userName, twitPass:this.password})
			this.scrim.hide()
			this.controller.showAlertDialog({
				onChoose: function(value) {
					this.controller.stageController.popScene({"twitToken":this.twitToken,"twitSecret":this.twitSecret})
				},
				title:'Authorization Complete',
				message:'Your "Add a beer" entries will be posted to Twitter automatically.',
				choices:[ {label:'OK', value:'OK', type:'color'} ]
			})
		}
		else authTokenFailure(transport)
	},
	authTokenFailure: function(transport){
		this.scrim.hide()
		this.controller.showAlertDialog({
			onChoose: function(value) {
				this.controller.stageController.popScene({"twitToken":"","twitSecret":""})
			},
			title:'Network Failure',
			message:'Unable to communicate with Twitter. Make sure you have an internet connection and that you have entered a valid user name and password. Please try again later. Status: '+transport.status,
			choices:[ {label:'OK', value:'OK', type:'color'} ]
		})
	},
	makeTimeStamp: function() {
		now = new Date().getTime()
		return Math.floor(now / 1000)
	},
	makeNonce: function() {
		var result = ''
		var nonceChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
		for( n = 0; n < 43; n++ ){
			var rnum = Math.floor(Math.random() * nonceChars.length);
			result += nonceChars.substring(rnum, rnum+1);
		}
		return result
	}
})