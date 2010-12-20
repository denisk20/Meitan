var items;
var itemsToDisplayCount = 3;
var lastUsedItems;
var currentAnimation;
var mouseOver = false;

var renewPeriod = 5000;
var fadeOutTime = 300;
var fadeInTime = 300;

Array.prototype.shuffle = function() {
	var s = [];
	while (this.length) {
		s.push(this.splice(Math.random() * this.length, 1));
	}
	while (s.length) {
		this.push(s.pop());
	}
	return this;
};

function main() {
	dojo.require("dojo.fx");
	items = dojo.query(".imageItem");
	lastUsedItems = items;

	shrinkDivs();
	prepareNewGoods();
	setInterval('prepareNewGoods()', renewPeriod);
}

//todo remove this
function shrinkDivs() {
	var divsToShrink = document.getElementsByTagName('div');
	for (i = 0; i < divsToShrink.length; i++) {
		var divToShrink = divsToShrink[i];
		var className = divToShrink.className;
		if (className == 'desc' || className == 'price') {
			if (divToShrink.innerHTML == '') {
				divToShrink.style.padding = '0';
			}
		}
	}
}

function prepareNewGoods() {
	if (mouseOver) {
		return;
	}
	var itemsToDisplay = [];
	if (items.length <= itemsToDisplayCount) {
		itemsToDisplay = items;
	} else {
		var indexesToUse = [];
		for (var i = 0; i < items.length; i++) {
			indexesToUse[i] = i;
		}
		indexesToUse.shuffle();

		var splicedIndexesToUse = indexesToUse.slice(0, itemsToDisplayCount);
		for (var i = 0; i < itemsToDisplayCount; i++) {
			itemsToDisplay[i] = items[splicedIndexesToUse[i]];
		}
	}

	var fadeouts = [];
	var disappears = [];

	var fadeins = [];
	var appears = [];

	for (var i = 0; i < lastUsedItems.length; i++) {
		var item = lastUsedItems[i];
//		currentAnimation = dojo.fadeOut({node: item ,duration: fadeOutTime, onEnd: function(){item.style.display = 'none';}});
		currentAnimation = dojo.fx.wipeOut({node: item ,duration: fadeOutTime});
		fadeouts[i] = currentAnimation;
		var setInVisible = dojo.animateProperty({
			node: item, duration: 1,
			properties: {
				display:		 { start: "block", end: "none" }
			}
		});
		disappears[i] = setInVisible;
		//				currentAnimation.play();
		//                item.style.display='none';
	}

	for (var i = 0; i < itemsToDisplay.length; i++) {
		var item = itemsToDisplay[i];
		//                item.style.display='block';
//		currentAnimation = dojo.fadeIn({node: item ,duration: fadeInTime, beforeBegin: function(){item.style.display = 'block';}});
		currentAnimation = dojo.fx.wipeIn({node: item ,duration: fadeInTime});
		fadeins[i] = currentAnimation;

		var setVisible = dojo.animateProperty({
			node: item, duration: 1,
			properties: {
				display:		 { start: "none", end: "block" }
			}
		});
		appears[i] = setVisible;
		//                currentAnimation.play();
	}
	currentAnimation = dojo.fx.chain(fadeouts.concat(fadeins));
	currentAnimation.play();
	lastUsedItems = itemsToDisplay;
}

window.onload = main;