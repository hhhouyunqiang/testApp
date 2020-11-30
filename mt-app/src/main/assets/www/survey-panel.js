var _front = RS2_SURVEY_FRONT.create();
var rs2_plugin = RS2_M_PLUGIN.create(_front);
_front.set_plugin(rs2_plugin);

var _mhandler = {
	send : function(e) {
	}
};

$(function() {
	/*var w = $(window);
	var _device = {
		width:w.width(),
		height:w.height(),
	};
	if(_device.width == 0) {
		_device.width = 320;
		_device.height = 480;
	}*/
	var calc = init_calc($(".calc-container"));
	rs2_plugin.set_calc(calc);
	var _device = {
		width:window.innerWidth || 320,
		height:window.innerHeight || 480
	};
  _front.init($('#survey1'), _device);
  rs2_plugin.call('onload');
});
