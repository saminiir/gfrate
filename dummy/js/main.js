$(document).ready(function(){

	var adjusted = 0;
	var adjustedHelp = "Press here to compensate your score by "

	function changeAdjusted() {
		if( adjusted < 0) {
			$('#hint').text(adjustedHelp + "(" + adjusted + ")");
			$('#hint').css('color', '#f5bd27');
		} else if(adjusted == 0) {
			$('#hint').text('Compensate rating by using the buttons below');
			$('#hint').css('color', '#fff');
		} else if( adjusted > 0) {
			$('#hint').text(adjustedHelp + "(" + adjusted + ")");
			$('#hint').css('color', '#6ccef3');
		}
	}

	$('#plus').click(function () {
		adjusted++;
		score = $('#score').text();
		scoreplus = parseFloat(score);
		scoreplus += 0.1;
		scoreplus = (Math.round( scoreplus * 10 ) / 10).toFixed(1);
		$('#score').text(scoreplus);

		changeAdjusted();
	})

	$('#minus').click(function () {
		adjusted--;
		score = $('#score').text();
		scoreplus = parseFloat(score);
		scoreplus -= 0.1;
		scoreplus = (Math.round( scoreplus * 10 ) / 10).toFixed(1);
		$('#score').text(scoreplus);

		changeAdjusted();
	})

	$('#middle').click(function () {
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

	jQuery("#score").fitText(0.5, { minFontSize: '100px', maxFontSize: '200px' })
	jQuery("#description").fitText(2, { minFontSize: '28px', maxFontSize: '200px' })
	jQuery("#rank").fitText(3, { minFontSize: '14px', maxFontSize: '200px' })
	jQuery("#hint").fitText(3, { minFontSize: '14px', maxFontSize: '200px' })
	jQuery("#plus").fitText(3, { minFontSize: '10px', maxFontSize: '20px' })
	jQuery("#minus").fitText(3, { minFontSize: '10px', maxFontSize: '20px' })

});