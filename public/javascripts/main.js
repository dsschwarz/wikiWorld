var myRenderer = {
  init:  function(system){ console.log("starting",system) },
  redraw:function(){ console.log("redraw") }
}
var sys = arbor.ParticleSystem();
sys.renderer = myRenderer
var rootUrl = "http://localhost:9000"

function nameFromUrl (url) {
	return url;
}

function createNode (url) {
	return sys.addNode({
		name: nameFromUrl(url),
		url: url
	});
}

function findOrCreateNode (url) {
	return sys.getNode(nameFromUrl(url)) || createNode(sys);
}

function getNodeInfo (url, onSuccess) {
	// get links
	var links = $.ajax(rootUrl + "/node?url=" + url, {
		success: function (result) {
			console.log(result)
			if (result.success == "false") {
				console.warn(result.message);
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