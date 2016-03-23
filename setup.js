function donemove() {
	var path;
	path = "move.html";
	$.getJSON( "http://127.0.0.1:8083/" +  path + "?" + encodeGameState(), update );	
}

function encodeGameState() {
	tboxfi = document.getElementById("fromi");
	tboxfj = document.getElementById("fromj");
	tboxti = document.getElementById("toi");
	tboxtj = document.getElementById("toj");
	fival = tboxfi.value;
	fjval = tboxfj.value;
	tival = tboxti.value;
	tjval = tboxtj.value;
	var toRet;
	toRet = "";
	toRet = toRet + fival + fjval + tival + tjval;
	return toRet;
}

function update(board) {
	arr = new Array(8);
	for(i = 0; i < 8; i++) arr[i] = new Array(8);
	for(i = 0; i < 8; i++) {
		for(j = 0; j < 8; j++) arr[i][j] = board.board[j + (8*i)];
	}
	brd = document.getElementById("checkerboard");
	temp = "";
	for(i = 0; i < 8; i++) {
		for(j = 0; j < 8; j++) temp = temp + arr[i][j] + " ";
		temp = temp + "<br>";
	}
	brd.innerHTML = temp;
}

function setup() {
	brd = document.getElementById("checkerboard");
	cboard = new Array(8);
	for(i = 0; i < 8; i++) cboard[i] = new Array(8);
	
	for(i = 0; i < 8; i++) {
		for(j = 0; j < 8; j++) cboard[i][j] = 0;
	}
	//since black starts first, computer is white
	//1: white piece, 2: black piece, 3: white king, 4: black king, 0: empty
    	
	//placing white pieces
    for(i = 0; i < 3; i++) {
		if(i%2 == 0) {
			for(j = 1; j < 8; j += 2) cboard[i][j] = 1;
		}
		else {
			for(j = 0; j < 7; j += 2) cboard[i][j] = 1;
		}
	}

	//placing black pieces
	for(i = 5; i < 8; i++) {
		if(i%2 == 0) {
			for(j = 1; j < 8; j += 2) cboard[i][j] = 2;
		}
		else {
			for(j = 0; j < 7; j += 2) cboard[i][j] = 2;
		}
	}
	temp = "";
	for(i = 0; i < 8; i++) {
		for(j = 0; j < 8; j++) temp = temp + cboard[i][j] + " ";
		temp = temp + "<br>";
	}
	brd.innerHTML = temp;
}

