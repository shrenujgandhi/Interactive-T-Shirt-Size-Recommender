$(function() {
	

	
	window.sizesData = [{
			"brandName":"Allen Solly",
			"sizes":[
				{"XS": "14"},
				{"S": "15"},
				{"M": "16"},
				{"L": "17"},
				{"XL": "18"},
				{"XXL": "19"}
			]
		},
		{
			"brandName":"UCB",
			"sizes":[
				{"XS": "15"},
				{"S": "16"},
				{"M": "17"},
				{"L": "18"},
				{"XL": "19"},
				{"XXL": "20"}
			]
		},
		{
			"brandName":"Wrangler",
			"sizes":[
				{"XS": "13"},
				{"S": "14"},
				{"M": "15"},
				{"L": "16"},
				{"XL": "17"},
				{"XXL": "18"}
			]
		},
		{
			"brandName":"AF",
			"sizes":[
				{"XS": "14"},
				{"S": "15"},
				{"M": "16"},
				{"L": "17"},
				{"XL": "18"},
				{"XXL": "19"}
			]
		},
		{
			"brandName":"CK",
			"sizes":[
				{"XS": "15"},
				{"S": "16"},
				{"M": "17"},
				{"L": "18"},
				{"XL": "19"},
				{"XXL": "20"}
			]
		}	
	];
});

window.recommendSizes = function(){
	var e = document.getElementById("fitbrand");
	var brand = e.options[e.selectedIndex].value;
	e = document.getElementById("fitsize");
	var size = e.options[e.selectedIndex].value;
	for(var i=0; i<sizesData.length; i++){	
		if(brand == sizesData[i].brandName){
			for(var j=0; j<sizesData[i].sizes.length; j++){
				var size_f = sizesData[i].sizes[j][size];
				if(size_f != undefined)
					getSize(size_f);
			}
		}
	}
};

window.getSize = function(sizeInInches){
	window.currentSizes = [];
	for(var i=0; i<sizesData.length; i++){
		var size = {
			"brand": sizesData[i].brandName,
			"fitSize": ""
		};
		var diff = 1000;
		for(var j=0; j<sizesData[i].sizes.length; j++){
			if(parseInt(sizesData[i].sizes[j]["XS"],0) - sizeInInches < diff){
				diff = Math.abs(parseInt(sizesData[i].sizes[j]["XS"],0) - sizeInInches);
				size.fitSize = "XS";
			}
			if(parseInt(sizesData[i].sizes[j]["S"],0) - sizeInInches < diff){
				diff = Math.abs(parseInt(sizesData[i].sizes[j]["S"],0) - sizeInInches);
				size.fitSize = "S";
			}
			if(parseInt(sizesData[i].sizes[j]["M"],0) - sizeInInches < diff){
				diff = Math.abs(parseInt(sizesData[i].sizes[j]["M"],0) - sizeInInches);
				size.fitSize = "M";
			}
			if(parseInt(sizesData[i].sizes[j]["L"],0) - sizeInInches < diff){
				diff = Math.abs(parseInt(sizesData[i].sizes[j]["L"],0) - sizeInInches);
				size.fitSize = "L";
			}
			if(parseInt(sizesData[i].sizes[j]["XL"],0) - sizeInInches < diff){
				diff = Math.abs(parseInt(sizesData[i].sizes[j]["XL"],0) - sizeInInches);
				size.fitSize = "XL";
			}
			if(parseInt(sizesData[i].sizes[j]["XXL"],0) - sizeInInches < diff){
				diff = Math.abs(parseInt(sizesData[i].sizes[j]["XXL"],0) - sizeInInches);
				size.fitSize = "XXL";
			}			
		}
		currentSizes.push(size);
	}
	
	var brandDivs = $(".book-name");
	var sizeDivs = $(".author");
	
	for(var i=0; i<brandDivs.length; i++){
		switch(brandDivs[i].innerHTML){
			case "Allen Solly":
				sizeDivs[i].innerHTML = "Recommended Size: "+ currentSizes[0].fitSize;
				break;
			case "UCB":
				sizeDivs[i].innerHTML = "Recommended Size: "+ currentSizes[1].fitSize;
				break;
			case "Wrangler":
				sizeDivs[i].innerHTML = "Recommended Size: "+ currentSizes[2].fitSize;
				break;
			case "A &amp; F":
				sizeDivs[i].innerHTML = "Recommended Size: "+ currentSizes[3].fitSize;
				break;
			case "Calvin Klein":
				sizeDivs[i].innerHTML = "Recommended Size: "+ currentSizes[4].fitSize;
				break;
			
			sizeDivs[i].style.fontWeight = "bold";
			sizeDivs[i].style.color = "#753131";
			
		}
	}
};