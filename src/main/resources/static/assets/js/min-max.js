$("#max").on("click",function() {
    min['max'] = max['value'];
    out_min['value'] = min['value']
})

$("#min").on("click",function() {
    if(max.value < min.value){
		max.value = 100
		out_max['value'] = 100
	}
})