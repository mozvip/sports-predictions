if (!Function.prototype.bind) {
  Function.prototype.bind = function (oThis) {
    if (typeof this !== "function") {
      // closest thing possible to the ECMAScript 5 internal IsCallable function
      throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");
    }

    var aArgs = Array.prototype.slice.call(arguments, 1), 
        fToBind = this, 
        fNOP = function () {},
        fBound = function () {
          return fToBind.apply(this instanceof fNOP && oThis
                                 ? this
                                 : oThis,
                               aArgs.concat(Array.prototype.slice.call(arguments)));
        };

    fNOP.prototype = this.prototype;
    fBound.prototype = new fNOP();

    return fBound;
  };
}

var application = {};
// Application Builder
(function(){
	// Define Global Helpers/Variables in here
	application._helpers = {
			classValueFetch : function(r,c) {
				/*
				 * Takes the class attribute of an element and
				 * checks each one for a key/value pair split on '-'
				 * returns the request key/value 
				 */
				var a,t;
				a = c.split(' ');
				$(a).each(function(i){
					t = $j(a).eq(i)[0];
					t = t.split('-');
					if(t){
						if(t[0] == r || t[0] === r) { r = t[1]; }
					}
				});
				return r;
			},
			isNumber : function(v) {
			var valid = "0123456789";
			 var temp;
			 var result = true;
			 if (v.length == 0) return false;
			 for (i = 0; i < v.length && result == true; i++)
					{
					temp = v.charAt(i);
					if (valid.indexOf(temp) == -1)
						 {
						 result = false;
						 }
					}
			 return result;
			},
			randomNumberRange: function(min,max){
				var range = (max - min) + 1;
				return Math.floor(Math.random()*range+min);
			}

	};
})();
application.Class = function(){};
// Based on code by John Resig: http://ejohn.org/blog/simple-javascript-inheritance/
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;

  // The base Class implementation (does nothing) 
  //this.Class = function(){};
 
  // Create a new Class that inherits from this class
  application.Class.extend = function(prop) {
    var _super = this.prototype;
   
    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;
   
    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" &&
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;
           
            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];
           
            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);       
            this._super = tmp;
           
            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }
   
    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }
   
    // Populate our constructed prototype object
    Class.prototype = prototype;
   
    // Enforce the constructor to be what we expect
    Class.constructor = Class;

    // And make this class extendable
    Class.extend = arguments.callee;
   
    return Class;
  };
})();