
var globalBeerItem
var globalCallbackSuccess
var globalCallbackFailure
var globalBeerDepot
// Statistics variables
var nBeers
var favorite
var mostDrunk
var favHour
var lastSeven
var lastMonth
var thisYear
var nstars = []
var nhour = []
var beerArray = []
var starArray = []
var nBeersArray = []
var drunk = []
var nDiffBeers
var whichPref
var prefNames = ['firstScreen','nextScreen','androidScreens','ratingReminders','vibrate','reminderDelay','twitPost',
								 'twitToken','twitSecret','facePost']

function getPreferences(selection, beerDepot, callbackSuccess, callbackFailure){
	whichPref = selection
	globalCallbackSuccess = callbackSuccess
	beerDepot.get('preferences', gotPrefs, callbackFailure)
}

function gotPrefs(results){
	var recordSize = Object.values(results).size();
	if(recordSize !== 0){
		eval('globalCallbackSuccess(results.'+whichPref+')')
	}
}

function getBeerList(beerDepot, callbackSuccess, callbackFailure){
	Mojo.Log.info('in getBeerList')
	beerDepot.get('beerList', callbackSuccess, callbackFailure)
	return(0)
}

function getBeerInfoList(beerDepot, callbackSuccess, callbackFailure){
	beerDepot.get('beerInfoList', callbackSuccess, callbackFailure)
	return(0)
}

function putToBeerList(beerDepot, beerItem, callbackSuccess, callbackFailure){
	globalBeerItem = beerItem
	globalCallbackSuccess = callbackSuccess
	globalCallbackFailure = callbackFailure
	globalBeerDepot = beerDepot
	Mojo.Log.info('in putToBeerList:'+Object.toJSON(globalBeerItem))
	getBeerList( beerDepot, gotBeerList, callbackFailure )
	return(0)
}

function gotBeerList(results){
	data = new Array(globalBeerItem)
	var recordSize = Object.values(results).size();
	if(recordSize !== 0){
		for( n = 0; n < results.length; n++ )
			data.push( results[n] )
	}
	globalBeerDepot.add('beerList', data, globalCallbackSuccess, globalCallbackFailure)
}

function putToBeerInfo(beerDepot, beerItem, callbackSuccess, callbackFailure){
	globalBeerItem = beerItem
	globalCallbackSuccess = callbackSuccess
	globalCallbackFailure = callbackFailure
	globalBeerDepot = beerDepot
	Mojo.Log.info('in putToBeerInfo:'+Object.toJSON(globalBeerItem))
	getBeerInfoList( beerDepot, gotBeerInfo, callbackFailure )
	return(0)
}

function gotBeerInfo(results){
	data = []
	var recordSize = Object.values(results).size();
	if(recordSize !== 0){
		isInList = false 
		for( n = 0; n < results.length; n++ ){
			//Mojo.Log.info(globalBeerItem.beerName+' == '+results[n].beerName)
			if( globalBeerItem.beerName == results[n].beerName ) // Update
				isInList = true 
			data.push( results[n] )
		}
		if( isInList == false ) // New beer
			data.push( globalBeerItem )
	}
	else // First beer
		data.push( globalBeerItem )
		//Mojo.Log.info(Object.toJSON(data))
		// Sort the beer data by name before storing
		data = beerSort( data, 'name' )
	globalBeerDepot.add('beerInfoList', data, globalCallbackSuccess, globalCallbackFailure)
}

function makeBeersDrunkList(beerDepot, callback, failCallback){
	//Mojo.Log.info('in makeBeersDrunkList')
	globalCallbackSuccess = callback
	globalCallbackFailure = failCallback
	getBeerInfoList( beerDepot, gotDrunkBeerData, failCallback  )
}

function gotDrunkBeerData(results){
	//Mojo.Log.info('in gotDrunkBeerData')
	data = []
	var recordSize = Object.values(results).size();
	if(recordSize !== 0){
		for( n = 0; n < results.length; n++ ){
			x = {"label":results[n].beerName,"command":results[n].beerName}
			data.push(x)
		}
	}
	globalCallbackSuccess( data )
}

function getBeerStatistics( beerDepot, callbackSuccess, callbackFailure ){
	globalCallbackSuccess = callbackSuccess
	globalCallbackFailure = callbackFailure
	globalBeerDepot = beerDepot
	nBeers = 0
	favorite = '-'
	mostDrunk = '-'
	favHour = '-'
	lastSeven = 0
	lastMonth = 0
	thisYear = 0
	nstars = []
	nhour = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
	beerArray = []
	starArray = []
	nBeersArray = []
	drunk = []
	nDiffBeers = 0
	getBeerInfoList( beerDepot, doStatsFirstPass, callbackFailure  )
}

function doStatsFirstPass( results ){
	//Mojo.Log.info('in doStatsFirstPass')
	var recordSize = Object.values(results).size();
	if(recordSize !== 0){
		nDiffBeers = results.length
		for( n = 0; n < results.length; n++ ){
			beerArray.push(results[n].beerName)
			drunk.push(0)
			starArray.push(0)
			nBeersArray.push(0)
		}
	}
	getBeerList( globalBeerDepot, doStatsSecondPass, globalCallbackFailure )
}

function doStatsSecondPass( results ){
	//Mojo.Log.info('in doStatsSecondPass')
	var recordSize = Object.values(results).size();
	if(recordSize !== 0){
		if( results.length != undefined )
			nBeers = results.length // no. beers
			for( n = 0; n < results.length; n++ ){
				beerDT = new String(results[n].drinkTimeDate)
				drinkHour = (beerDT.substr(11,2))-0
				//Mojo.Log.info('Beer #'+n+' Hour='+drinkHour)
				nhour[drinkHour]++
				drinkYear = (beerDT.substr(0,4))
				drinkMonth = (beerDT.substr(5,2))-1
				drinkDay = (beerDT.substr(8,2))
				drinkMin = (beerDT.substr(14,2))
				drinkSec = (beerDT.substr(17,2))
				drinkDate = new Date(drinkYear,drinkMonth,drinkDay,drinkHour,drinkMin,drinkSec)
				//Mojo.Log.info('Beer #'+n+' Date='+drinkDate.toDateString())
				today = new Date()
				thisYearIs = today.getFullYear()
				thisMonthIs = today.getMonth()
				sevenAgoIs = new Date().setDate(today.getDate()-7)
				if( drinkYear == thisYearIs ) thisYear++
				if( drinkMonth == thisMonthIs ) lastMonth++
				if( drinkDate > sevenAgoIs ) lastSeven++
				for( m = 0; m < beerArray.length; m++ ){
					if( results[n].beerName == beerArray[m] ){
						starArray[m] += (results[n].stars)-0
						nBeersArray[m]++
						drunk[m] = 1
					}
				}
			}
			nDiffBeers = 0
			for( n = 0; n < drunk.length; n++ )
				nDiffBeers += drunk[n]
			mostBeers = 0
			for( n = 0; n < 24; n++ ){
				if( nhour[n] > 0 && nhour[n] > mostBeers ){
					mostBeers = nhour[n]
					if( n > 11 ) var ampm = 'PM'
					else ampm = 'AM'
					if( n > 12 ) favHour = ''+n-12+' '+ampm
					else if( n == 0 ) favHour = '12 AM'
					else favHour = ''+n+' '+ampm
				}
			}
			mostStars = 0
			mostBeers = 0
			for( n = 0; n < beerArray.length; n++ ){
				if( starArray[n] > 0 && starArray[n]/nBeersArray[n] > mostStars ){
					mostStars = starArray[n]/nBeersArray[n]
					favorite = beerArray[n]
				}
				if( nBeersArray[n] > 0 && nBeersArray[n] > mostBeers ){
					mostBeers = nBeersArray[n]
					mostDrunk = beerArray[n]
				}
			}
		}
	globalCallbackSuccess( nBeers, nDiffBeers, favorite, mostDrunk, favHour, lastSeven, lastMonth, thisYear )
}
	function beerSort( sortArray, sortBy ) {
		indexArray = new Array()
		for( n = 0; n < sortArray.length; n++ )
			indexArray.push(n) // Make array of current index
		for( var n = 0; n < sortArray.length-1; n++ ){
			var upper = new Array(sortArray[indexArray[n]])
			for( var m = n+1; m < sortArray.length; m++ ){
				var lower = new Array(sortArray[indexArray[m]])
				if( sortBy == 'name' ){
					var temp = new Array(upper[0].beerName, lower[0].beerName)
					var temp2 = new Array(upper[0].beerName, lower[0].beerName).sort()
				}
				else if( sortBy == 'date' ){
					var temp = new Array(upper[0].drinkTimeDate, lower[0].drinkTimeDate)
					var temp2 = new Array(upper[0].drinkTimeDate, lower[0].drinkTimeDate).sort()
				}
				else{ // rating
					var temp = new Array(upper[0].stars, lower[0].stars)
					var temp2 = new Array(upper[0].stars, lower[0].stars).sort()
				}
				if( temp[0] != temp.sort()[0] ){
					t = indexArray[n]
					indexArray[n] = indexArray[m]
					indexArray[m] = t
					upper = lower
				}
			}
		}
		if( sortBy != 'name' ) indexArray.reverse()
		data = new Array()
		for( n = 0; n < indexArray.length; n++ ){
			//sortArray[indexArray[n]].uniqueID = n
			data.push( sortArray[indexArray[n]] )
		}
		return( data )
	}