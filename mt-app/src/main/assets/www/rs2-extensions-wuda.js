(function WUDA_EXTENSION(CTRL) {

 function _inputed(v, def) {
  var n = 0;
  if(v) {
   for(var i in v) {
    if(v.hasOwnProperty(i)) {
     var optv = v[i];
     if(def) {
      if(def == optv) {
       n++;
      }
     } else if(optv != undefined) {
      n++;
     }
    }
   }
  }
  return n;
 }

 function _answered(data, qid) {
  var required = data.qprop(qid, 'required');
  var total = 0, n = 0;
  if(required !== true) {
   total = 1;
   var v = data.qval(qid);
   var type = data.qprop(qid, 'type');
   switch(type) {
   case RS2_T_SELECT:
   case RS2_T_MULSELECT:
    n = _inputed(v, '1') > 0 ? 1 : 0;
    break;
   case RS2_T_ASSIGNMENT: {
    var opts = data.qprop(qid, 'options');
    if(opts) {
     total = opts.length;
     n = _inputed(v);
    }
   }
    break;
   case RS2_T_MATRIX: {
    n = _inputed(v);
   }
    break;
   case RS2_T_LABEL:
   case RS2_T_UNKNOWN: {
    total = 0;
   }
    break;
   default:
    var vt = typeof v;
    if(vt === 'object') {
     n =  _inputed(v) > 0 ? 1 : 0;
     } else if(vt === 'string') {
     n = v.length > 0 ? 1 : 0;
    } else if (v !== undefined) {
     n = 1;
    }
    break;
   }
  }
  return [total, n];
 }

 CTRL.on_event('page_finished', function(state, data) {
  var hist = state.history();
  var total = 0;
  var n = 0;
  var result = 1;
  for(var i = 0; i < hist.length; ++i) {
   var qid = hist[i].qid;
   var type = data.qprop(qid, 'type');
   if(type === RS2_T_GROUP) {
    var subqs = data.qprop(qid, 'questions');
    for(var j = 0; j < subqs.length; ++j) {
     var r = _answered(data, subqs[j]);
     total += r[0];
     n += r[1];
    }
   } else {
    var r = _answered(data, qid);
    total += r[0];
    n += r[1];
   }
  }
  if(total > 0) {
   result = n / total;
  }
  data.set_sys_vval("completion_rate", result);
  console.log("completion rate " + data.sys_vval("completion_rate"));
 });
 /*

M.survey_remark_flag = function(sprops, iprops) {
  if(iprops.edit_type === RS2_I_EDIT_CHECK) {
   var s = sprops.remark_style;
   if(s === 'question') {
    return RS2_I_REMARK_QUESTION;
   } else if(s === 'page') {
    return RS2_I_REMARK_PAGE;
   } else {
    return RS2_I_REMARK_NONE;
   }
  } else {
   return RS2_I_REMARK_NONE;
  }
 };
M.remark_collapsed =  function(q, v) {
  var t = q.type;
  switch(t) {
  case RS2_T_SELECT:
  case RS2_T_MULSELECT:
   return _inputed(v, '1') > 0
  case RS2_T_ASSIGNMENT:
   return _inputed(v) === q.options.length;
  case RS2_T_MATRIX:
   return true;
  default:
   if(v === undefined) {
    return false;
   } else {
    if(v == "") {
     return false;
    } else if(v.length === 0) {
     return false;
    } else {
     return _inputed(v) > 0
    }
   }
  }
 }*/;
}(RS2_CORE_SURVEY_CTRL));


function isInteger(num){
    return Math.floor(num) === num;
}
function isPositiveInteger(num){

    var parnt = /^[1-9]\d*(\.\d+)?$/;
    if(parnt.exec(num)){
        return true;
    }
    return false;
}
function isChOrEn(str){

    if(!(Math.floor(str) === str)){
        var isNum = /[0-9]/;
        if(isNum.test(str)){
            return false;
        }else{
            return true;
        }
    }
    return false;
}

function isEn(str){
    var reg=new RegExp("^[a-zA-Z]+$");
    if(reg.test(str)){
        return true;
    }
    return false;
}
function isCh(str){
    var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
    if(reg.test(str)){
        return true;
    }
    return false;
}
function isNum(val){

    var regPos = /^\d+(\.\d+)?$/; //非负浮点数
    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
    if(regPos.test(val) || regNeg.test(val)){
        return true;
    }else{
        return false;
    }

}