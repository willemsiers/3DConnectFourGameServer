<%--
  Created by IntelliJ IDEA.
  User: Rogier
  Date: 17-01-17
  Time: 15:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>3D Connect 4 Game Server</title>

    <script src="jquery-3.1.1.js"></script>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"
          integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"
            integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa"
            crossorigin="anonymous"></script>
    <script>
        interval = null;
        window.init = function () {
            setInterval(function () {
                console.log('refresh lobby');
                var xmlHttp = null;
                xmlHttp = new XMLHttpRequest();
                xmlHttp.open("GET", "/games?action=lobby", false);
                xmlHttp.send(null);
                var data = $.parseJSON(xmlHttp.responseText);
                $('#lobbytable > tr ').remove();
                $.each(data, function (key, value) {
                    $('#lobbytable').append("<tr> <td> <button onclick=\"start_game(" + value['game number'] + ", \'" + value.player1 + "\', \' " + value.player2 + "\') \" id=\"game_" + value['game number'] + "\" type=\"button\" class=\"btn btn-default static-size\"> <div class=\"row\"> <div class=\"col-md-5\"> <h3 align=\"left\">" + value.player1 + "</h2> </div> <div class=\"col-md-5\"> <h3 align = \"center\">" + value.player2 + "</h2> </div> <div class=\"col-md-2\"> <h3 align=\"right\">" + value['game number'] + "</h2> </div> </div> </button> </td> </tr>")

                })
            }, 3000)

        };
        setMove = function (color, x, y, z) {
            var col = "yellow";
            if (color == "R") {
                col = "red";
            }
            (document.getElementById("c" + x + y + z)).className += " " + col;
        };
        clearBoard = function () {
            $("td>div").each(function () {
                $(this).removeClass('yellow');
                $(this).removeClass('red')
            });
        };
        getData = function (game_id) {
            var xmlHttp = null;
            xmlHttp = new XMLHttpRequest();
            xmlHttp.open("GET", "/games?action=game&id=" + game_id, false);
            xmlHttp.send(null);
            var data = $.parseJSON(xmlHttp.responseText);
            return data;
        };
        setBoard = function (game_id) {
            console.log("board " + game_id);
            $("#winbanaan").hide();
            $('#wintext').hide();
            data = getData(game_id);
            if (data.winner) {
                console.log(data.winner);
                $('#winbanaan').show();
                $('#wintext').text(data.winner + " HEEFT GEWONNEN");
                $('#wintext').show()
            }
            var grid = data.grid;
            $.each(grid, function (ind, val) {
                $.each(val, function (ind2, val2) {
                    $.each(val2, function (ind3, move) {
                        if (move !== "_") {
                            setMove(move, ind, ind2, ind3);
                        }
                    })
                })
            })
        }
        start_game = function (game_id, player1, player2) {
            console.log(player1);
            $('#player-1').text(player1);
            $('#player-2').text(player2);
            $('#vstitle').text("VS");
            if (interval) {
                clearInterval(interval)
            }
            clearBoard();
            interval = setInterval(function () {
                    setBoard(game_id)
                }
                , 1000)
        }
    </script>
</head>

<style>
    body {
        background-size: cover;
        background-image: url("./download.jpg");
    }

    .red {
        background-color: red;
    }

    .yellow {
        background-color: yellow;
    }

    td > div {
        margin-top: 5px !important;
        border-style: solid;
        border-width: 1px
    }

    .cell {
        width: 150px;
        height: 150px;
        border-style: solid;
        border-width: 2px;
    }

    .static-size {
        width: 500px;
    }

    td > .c4 {
        height: 20px;
        width: 80px;
        margin: auto;
        margin-bottom: 0;
    }

    td > .c3 {
        height: 20px;
        width: 65px;
        margin: auto;
    }

    td > .c2 {
        height: 20px;
        width: 50px;
        margin: auto;
    }

    td > .c1 {
        height: 20px;
        width: 35px;
        margin: auto;
    }
</style>
<body onload='init()'>


<div class="container-fluid">
    <div class="row">
        <div class="col-md-3">
            <img style="width:100px" src="/banaan.gif" alt="">
            <img style="width:100px" src="/banaan.gif" alt="">
            <img style="width:100px" src="/banaan.gif" alt="">

            <img style="width:100px" src="/banaan.gif" alt="">
        </div>
        <div class="col-md-6">
            <br>
            <h1 align='center' style='font-family: "Comic Sans MS" '>CHRISTIAANS 4-OP-EEN-RIJ-3D EPIC NEGA VIEWER</h1>
        </div>
        <div class="col-md-3">
            <img style="width:100px" src="/banaan.gif" alt="">
            <img style="width:100px" src="/banaan.gif" alt="">
            <img style="width:100px" src="/banaan.gif" alt="">

            <img style="width:100px" src="/banaan.gif" alt="">
        </div>
    </div>

    <br> <br>
    <div class="row">
        <div class="col-md-2">
            <br><br><br>
            <img class="img-responsive" src="Congratulations_you_won.gif" alt="">
        </div>
        <div class="col-md-4">
            <br><br> <br><br><br>
            <div display="none" id="banaanwin">
                <h1 style=" display:none" id="wintext"></h1>
                <img class="img-responsive" id="winbanaan" src="frans.jpg" style=" display:none; position:absolute"
                     alt="">

            </div>
            <table id="lobbytable" class="table table-striped">
                <!--   -->
            </table>
        </div>
        <div class="col-md-4">
            <!-- <br><br>

            <div class="row">
                <div style="
                background-color: aliceblue;" class="col-md-4">
                <h2 style="color:red" align="center" id="player-1"></h2>
            </div>
            <div class="col-md-4">
                <h2  align="center" id="vstitle">NO GAME STARTED</h2>
            </div>
            <div style="
            background-color: pink;" class="col-md-4">
            <h2 style="color:yellow" 	align="center" id="player-2"></h2>
        -->
            <table>
                <tr class="row-1">
                    <td class="cell">
                        <div id="c033" class="c1"></div>
                        <div id="c032" class="c2"></div>
                        <div id="c031" class="c3"></div>
                        <div id="c030" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c133" class="c1"></div>
                        <div id="c132" class="c2"></div>
                        <div id="c131" class="c3"></div>
                        <div id="c130" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c233" class="c1"></div>
                        <div id="c232" class="c2"></div>
                        <div id="c231" class="c3"></div>
                        <div id="c230" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c333" class="c1"></div>
                        <div id="c332" class="c2"></div>
                        <div id="c331" class="c3"></div>
                        <div id="c330" class="c4"></div>
                    </td>
                </tr>
                <tr class="row-2">
                    <td class="cell">
                        <div id="c023" class="c1"></div>
                        <div id="c022" class="c2"></div>
                        <div id="c021" class="c3"></div>
                        <div id="c020" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c123" class="c1"></div>
                        <div id="c122" class="c2"></div>
                        <div id="c121" class="c3"></div>
                        <div id="c120" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c223" class="c1"></div>
                        <div id="c222" class="c2"></div>
                        <div id="c221" class="c3"></div>
                        <div id="c220" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c323" class="c1"></div>
                        <div id="c322" class="c2"></div>
                        <div id="c321" class="c3"></div>
                        <div id="c320" class="c4"></div>
                    </td>
                </tr>
                <tr class="row-3">
                    <td class="cell">
                        <div id="c013" class="c1"></div>
                        <div id="c012" class="c2"></div>
                        <div id="c011" class="c3"></div>
                        <div id="c010" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c113" class="c1"></div>
                        <div id="c112" class="c2"></div>
                        <div id="c111" class="c3"></div>
                        <div id="c110" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c213" class="c1"></div>
                        <div id="c212" class="c2"></div>
                        <div id="c211" class="c3"></div>
                        <div id="c210" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c313" class="c1"></div>
                        <div id="c312" class="c2"></div>
                        <div id="c311" class="c3"></div>
                        <div id="c310" class="c4"></div>
                    </td>
                </tr>
                <tr class="row-4">
                    <td class="cell">
                        <div id="c003" class="c1"></div>
                        <div id="c002" class="c2"></div>
                        <div id="c001" class="c3"></div>
                        <div id="c000" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c103" class="c1"></div>
                        <div id="c102" class="c2"></div>
                        <div id="c101" class="c3"></div>
                        <div id="c100" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c203" class="c1"></div>
                        <div id="c202" class="c2"></div>
                        <div id="c201" class="c3"></div>
                        <div id="c200" class="c4"></div>
                    </td>
                    <td class="cell">
                        <div id="c303" class="c1"></div>
                        <div id="c302" class="c2"></div>
                        <div id="c301" class="c3"></div>
                        <div id="c300" class="c4"></div>
                    </td>
                </tr>
            </table>
        </div>
        <div class="col-md-2">
            <img class="img-responsive" src="frans.jpg" alt="">
        </div>
    </div>


    <div class="row">
        <div class="col-md-4"><img class="img-responsive" src="download (1).jpg" alt=""></div>
        <div class="col-md-4"><img class="img-responsive" src="ynoLs0I.gif" alt=""></div>
        <div class="col-md-4"></div>
    </div>
</div>
</body>
</html>
