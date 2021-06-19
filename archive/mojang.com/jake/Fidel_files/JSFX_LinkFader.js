var _____WB$wombat$assign$function_____ = function (name) { return (self._wb_wombat && self._wb_wombat.local_init && self._wb_wombat.local_init(name)) || self[name]; };
if (!self.__WB_pmw) { self.__WB_pmw = function (obj) { this.__WB_source = obj; return this; } }
{
    let window = _____WB$wombat$assign$function_____("window");
    let self = _____WB$wombat$assign$function_____("self");
    let document = _____WB$wombat$assign$function_____("document");
    let location = _____WB$wombat$assign$function_____("location");
    let top = _____WB$wombat$assign$function_____("top");
    let parent = _____WB$wombat$assign$function_____("parent");
    let frames = _____WB$wombat$assign$function_____("frames");
    let opener = _____WB$wombat$assign$function_____("opener");

    /******************************************************************* 
    * File    : JSFX_LinkFader.js  © JavaScript-FX.com
    * Created : 2002/09/05 
    * Author  : Roy Whittle  (Roy@Whittle.com) www.Roy.Whittle.com 
    * Purpose : To create a more dynamic a:hover using fading
    * History 
    * Date         Version        Description 
    * 2002-09-05	1.0		First version
    ***********************************************************************/
    /*** Create some global variables ***/
    if (!window.JSFX) JSFX = new Object();

    var LinkFadeInStep = 20;
    var LinkFadeOutStep = 5;
    var LinkEndColor = "686764"

    var LinkStartColor = "FFFFFF";
    var LinkFadeRunning = false;

    document.onmouseover = theOnOver;
    document.onmouseout = theOnOut;
    if (document.captureEvents)
        document.captureEvents(Event.MOUSEOVER | Event.MOUSEOUT);

    /***********************************************
    *
    * Function    : getColor
    *
    * Parameters  :	start - the start color (in the form "RRGGBB" e.g. "FF00AC")
    *			end - the end color (in the form "RRGGBB" e.g. "FF00AC")
    *			percent - the percent (0-100) of the fade between start & end
    *
    * returns	  : color in the form "#RRGGBB" e.g. "#FA13CE"
    *
    * Description : This is a utility function. Given a start and end color and
    *		    a percentage fade it returns a color in between the 2 colors
    *
    * Author	  : www.JavaScript-FX.com
    *
    *************************************************/
    function hex2dec(hex) { return (parseInt(hex, 16)); }
    function dec2hex(dec) { return (dec < 16 ? "0" : "") + dec.toString(16); }
    function getColor(start, end, percent) {

        var r1 = hex2dec(start.slice(0, 2));
        var g1 = hex2dec(start.slice(2, 4));
        var b1 = hex2dec(start.slice(4, 6));

        var r2 = hex2dec(end.slice(0, 2));
        var g2 = hex2dec(end.slice(2, 4));
        var b2 = hex2dec(end.slice(4, 6));

        var pc = percent / 100;

        var r = Math.floor(r1 + (pc * (r2 - r1)) + .5);
        var g = Math.floor(g1 + (pc * (g2 - g1)) + .5);
        var b = Math.floor(b1 + (pc * (b2 - b1)) + .5);

        return ("#" + dec2hex(r) + dec2hex(g) + dec2hex(b));
    }
    /************************************************/
    JSFX.getCurrentElementColor = function (el) {
        var result = LinkStartColor;

        if (el.currentStyle)
            result = (el.currentStyle.color);
        else if (document.defaultView)
            result = (document.defaultView.getComputedStyle(el, '').getPropertyValue('color'));
        else if (el.style.color) //Opera
            result = el.style.color;

        if (result.charAt(0) == "#")      //color is of type #rrggbb
            result = result.slice(1, 8);
        else if (result.charAt(0) == "r") //color is of type rgb(r, g, b)
        {
            var v1 = result.slice(result.indexOf("(") + 1, result.indexOf(")"));
            var v2 = v1.split(",");
            result = (dec2hex(parseInt(v2[0])) + dec2hex(parseInt(v2[1])) + dec2hex(parseInt(v2[2])));
        }

        return result;
    }
    JSFX.findTagIE = function (el) {
        while (el && el.tagName != 'A')
            el = el.parentElement;
        return (el);
    }
    JSFX.findTagNS = function (el) {
        while (el && el.nodeName != 'A')
            el = el.parentNode;
        return (el);
    }
    function theOnOver(e) {
        var lnk;
        if (window.event)
            lnk = JSFX.findTagIE(event.srcElement);
        else
            lnk = JSFX.findTagNS(e.target);

        if (lnk)
            JSFX.linkFadeUp(lnk);
    }
    JSFX.linkFadeUp = function (lnk) {
        if (lnk.state == null) {
            lnk.state = "OFF";
            lnk.index = 0;
            lnk.startColor = JSFX.getCurrentElementColor(lnk);
            lnk.endColor = LinkEndColor;
        }

        if (lnk.state == "OFF") {
            lnk.state = "FADE_UP";
            JSFX.startLinkFader();
        }
        else if (lnk.state == "FADE_UP_DOWN"
            || lnk.state == "FADE_DOWN") {
            lnk.state = "FADE_UP";
        }
    }
    function theOnOut(e) {
        var lnk;
        if (window.event)
            lnk = JSFX.findTagIE(event.srcElement);
        else
            lnk = JSFX.findTagNS(e.target);

        if (lnk)
            JSFX.linkFadeDown(lnk);
    }
    JSFX.linkFadeDown = function (lnk) {
        if (lnk.state == "ON") {
            lnk.state = "FADE_DOWN";
            JSFX.startLinkFader();
        }
        else if (lnk.state == "FADE_UP") {
            lnk.state = "FADE_UP_DOWN";
        }
    }
    JSFX.startLinkFader = function () {
        if (!LinkFadeRunning)
            JSFX.LinkFadeAnimation();
    }
    /*******************************************************************
    *
    * Function    : LinkFadeAnimation
    *
    * Description : This function is based on the Animate function
    *		        of animate.js (animated rollovers).
    *		        Each fade object has a state. This function
    *		        modifies each object and changes its state.
    *****************************************************************/
    JSFX.LinkFadeAnimation = function () {
        LinkFadeRunning = false;
        for (i = 0; i < document.links.length; i++) {
            var lnk = document.links[i];
            if (lnk.state) {
                if (lnk.state == "FADE_UP") {
                    lnk.index += LinkFadeInStep;
                    if (lnk.index > 100)
                        lnk.index = 100;
                    lnk.style.color = getColor(lnk.startColor, lnk.endColor, lnk.index);

                    if (lnk.index == 100)
                        lnk.state = "ON";
                    else
                        LinkFadeRunning = true;
                }
                else if (lnk.state == "FADE_UP_DOWN") {
                    lnk.index += LinkFadeOutStep;
                    if (lnk.index > 100)
                        lnk.index = 100;
                    lnk.style.color = getColor(lnk.startColor, lnk.endColor, lnk.index);

                    if (lnk.index == 100)
                        lnk.state = "FADE_DOWN";
                    LinkFadeRunning = true;
                }
                else if (lnk.state == "FADE_DOWN") {
                    lnk.index -= LinkFadeOutStep;
                    if (lnk.index < 0)
                        lnk.index = 0;
                    lnk.style.color = getColor(lnk.startColor, lnk.endColor, lnk.index);

                    if (lnk.index == 0)
                        lnk.state = "OFF";
                    else
                        LinkFadeRunning = true;
                }
            }
        }
        /*** Check to see if we need to animate any more frames. ***/
        if (LinkFadeRunning)
            setTimeout("JSFX.LinkFadeAnimation()", 40);
    }


}