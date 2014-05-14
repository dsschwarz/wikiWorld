var myRenderer = function(canvasSelector){
    var canvas = $(canvasSelector).get(0);
    var ctx = canvas.getContext("2d");
    var particleSystem = null;

    var that = {
		init:  function(system){
			particleSystem = system;
		    particleSystem.screen({padding:[100, 60, 60, 60], // leave some space at the bottom for the param sliders
		                          step:.02}) // have the ‘camera’ zoom somewhat slowly as the graph unfolds 
		    $(window).resize(that.resize)
		    that.resize()
		},
		redraw:function(){
			if (particleSystem===null) return;

			console.log("Rendering")

			ctx.clearRect(0,0, canvas.width, canvas.height)
			ctx.strokeStyle = "#d3d3d3"
			ctx.lineWidth = 1
			ctx.beginPath()
			particleSystem.eachEdge(function(edge, pt1, pt2){
	            // draw a line from pt1 to pt2
	            ctx.moveTo(pt1.x, pt1.y)
	            ctx.lineTo(pt2.x, pt2.y)
	        })
			ctx.stroke()

			particleSystem.eachNode(function(node, pt){
				ctx.fillStyle = "orange"
				ctx.beginPath();
				ctx.arc(pt.x,pt.y,5,0,2*Math.PI);
				ctx.fill()
			})    		
		},
		resize:function(){
			var w = $(window).width(),
			h = $(window).height();
	        canvas.width = w; canvas.height = h // resize the canvas element to fill the screen
	        particleSystem.screenSize(w,h) // inform the system so it can map coords for us
	        that.redraw()
    }
	}
	return that
}
var sys = arbor.ParticleSystem(4000, 500, 0.5, false, 55, 0.01);
$(document).ready(function () {
	sys.renderer = myRenderer("canvas.viewport");
})
var rootUrl = "http://localhost:9000"
var lastMousePosition = {x: 0, y: 0};

function nameFromUrl (url) {
	return url;
}

function createNode (url, loc) {
	return sys.addNode(nameFromUrl(url), {url: url});
}

function findOrCreateNode (url) {
	return sys.getNode(nameFromUrl(url)) || createNode(url);
}

function getNodeInfo (url, onSuccess) {
	// get links
	var links = $.ajax(rootUrl + "/node?url=" + url, {
		success: function (result) {
			console.log(result)
			if (result.success == "false") {
				console.warn(result.message);
				if(result.message == "Getting Links") {
					getNodeInfo(url, onSuccess);
				}
			} else {
				for (var i = result.length - 1; i >= 0; i--) {
					var link = result[i];

					sys.addEdge(findOrCreateNode(link.start), findOrCreateNode(link.end));
				};
			}
		}
	});
}

$(document).ready(function () {
	$("#getNode").click(function (event) {
		console.log("click")
		getNodeInfo($("#urlInput").val());
	})
})