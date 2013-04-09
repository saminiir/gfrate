$(document).ready(function(){

	var adjusted = 0;
	var adjustedHelp = "Tap here to compensate rating by "

	function onLoad() {
		document.addEventListener("deviceready", onDeviceReady, false);
	}

	//initialize localStorage
	function onDeviceReady() {
		if(!localStorage.getItem("visits")) {
			localStorage.setItem("visits", 0);	
		} else {
			adjusted = Number(localStorage.getItem("visits"));
			changeAdjusted();
		}
	}

	function onPause() {
		localStorage.setItem("visits", adjusted);
	}

	function onResume() {
		if(!localStorage.getItem("visits")) {
			localStorage.setItem("visits", 0);	
		} else {
			adjusted = Number(localStorage.getItem("visits"));
			changeAdjusted();
		}
	}

	function onUnLoaded() {
		localStorage.setItem("visits", adjusted);
	}

	// fastclick
	window.addEventListener('load', function() {
	    new FastClick(document.body);
	}, false);

	function changeAdjusted() {
		if( adjusted < 0) {
			$('#hint').html(adjustedHelp + "<span class='big'>(" + adjusted + ")</span>");
			$('#hint').css('color', '#6ccef3');
		} else if(adjusted == 0) {
			$('#hint').text('How has your partner behaved recently?');
			$('#hint').css('color', '#fff');
		} else if( adjusted > 0) {
			$('#hint').html(adjustedHelp + "<span class='big'>(+" + adjusted + ")</span>");
			$('#hint').css('color', '#f5bd27');
		}

	}

	$('#plus').click(function () {
		
		adjusted++;
		$('#hint').effect("pulsate", { times:1 }, 0).stop(false, true);		
	
		changeAdjusted();
	})

	$('#minus').click(function () {
		
		adjusted--;
		$('#hint').effect("pulsate", { times:1 }, 0).stop(false, true);

		changeAdjusted();
	})

	$('#hint').click(function () {
		if( adjusted != 0 ) {
			$.ajax({
			type: "POST",
			url: url,
			data: {adjusted: adjusted},
			success: success,
			dataType: dataType
			});
		}
	})

	jQuery("#score").fitText(0.7, { minFontSize: '150px', maxFontSize: '400px' })
	jQuery("#description").fitText(2, { minFontSize: '27px', maxFontSize: '54px' })
	jQuery("#rank").fitText(3, { minFontSize: '18px', maxFontSize: '36px' })
	jQuery("#hint").fitText(3, { minFontSize: '18px', maxFontSize: '36px' })

	x$('.button').on('touchstart', function(e) { 
	    x$(e.currentTarget).addClass('button-active'); 
	} );
	x$('.button').on('touchend', function(e) { 
	    x$(e.currentTarget).removeClass('button-active'); 
	} );

	//Unix time
    var foo = new Date; // Generic JS date object
    var unixtime_ms = foo.getTime(); // Returns milliseconds since the epoch
    var unixtime = parseInt(unixtime_ms / 1000);

    var callBackURL = "http://esajuhana.com"; //oob for now
    var nonce = "12342897";

    //Create Signature Base String using formula
    var baseSign = "POST" + "&" + encodeURIComponent("https://api.login.yahoo.com/oauth/v2/get_request_token").toString() + "&"
     + encodeURIComponent("oauth_callback") + "%3D" + encodeURIComponent(callBackURL)
     + "%26"
     + encodeURIComponent("oauth_consumer_key") + "%3D" + encodeURIComponent("dj0yJmk9WmYweklNRzl0OTJZJmQ9WVdrOU5ucGFXR1ZJTXpnbWNHbzlNVEV6TWpBd016STJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xOQ--")
     + "%26"
     + encodeURIComponent("oauth_nonce") + "%3D" + encodeURIComponent(nonce)
     + "%26"
     + encodeURIComponent("oauth_signature_method") + "%3D" + encodeURIComponent("HMAC-SHA1")
     + "%26"
     + encodeURIComponent("oauth_timestamp") + "%3D" + encodeURIComponent(unixtime)
     + "%26"
     + encodeURIComponent("oauth_version") + "%3D" + encodeURIComponent("1.0");

    //Create Signature, With consumer Secret key we sign the signature base string
    var signature = b64_hmac_sha1("c130b0f630066613b4cd4fff0957daa72594c511", baseSign);

    //Build headers from signature
    var jsonData = JSON.stringify({
        Authorization: {
            oauth_nonce: nonce,
            oauth_callback: encodeURIComponent(callBackURL),
            oauth_signature_method: "HMAC-SHA1",
            oauth_timestamp: unixtime,
            oauth_consumer_key: "dj0yJmk9WmYweklNRzl0OTJZJmQ9WVdrOU5ucGFXR1ZJTXpnbWNHbzlNVEV6TWpBd016STJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xOQ--",
            oauth_signature: signature,
            oauth_version: "1.0"
        }
    });

    //Request Access Token
    $.ajax({
        url: "http://api.twitter.com/oauth/request_token",
        type: "post",
        headers: jsonData,
        dataType: "jsonp",
        success: function (data) {
            alert(data);
        },
        error: function (data) {
            alert("Error");
        }
	
});