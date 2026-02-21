import socket
import minescript
import sys
import time
import json

def mine_block(x, y, z, direction):
    block = minescript.getblock(x, y, z)
    if block != "minecraft:air":
        print(f"Mining block at ({x}, {y}, {z}): {block}")
        minescript.player_look_at(x + 0.5, y + 0.5, z + 0.5)
        if minescript.player_get_targeted_block(4) == None:
            go_one_block(direction)
            minescript.player_look_at(x + 0.5, y + 0.5, z + 0.5)
        minescript.player_press_attack(True)
        while block != "minecraft:air":
            block = minescript.getblock(x, y, z)
        minescript.player_press_attack(False)
        # Add mining action here if needed

def go_one_block(direction):
    print("start walk")

    match direction:
        case "+x":
            minescript.player_set_orientation(90, 0)
            minescript.press_key_bind("key.forward", True)
            time.sleep(0.25)  # Adjust sleep time as necessary for one block movement
            minescript.press_key_bind("key.forward", False)

        case "-x":
            minescript.player_set_orientation(-90, 0)
            minescript.press_key_bind("key.forward", True)
            time.sleep(0.25)  # Adjust sleep time as necessary for one block movement
            minescript.press_key_bind("key.forward", False)

        case "+z":
            minescript.player_set_orientation(180, 0)
            minescript.press_key_bind("key.forward", True)
            time.sleep(0.25)  # Adjust sleep time as necessary for one block movement
            minescript.press_key_bind("key.forward", False)

        case "-z":
            minescript.player_set_orientation(0, 0)
            minescript.press_key_bind("key.forward", True)
            time.sleep(0.25)  # Adjust sleep time as necessary for one block movement
            minescript.press_key_bind("key.forward", False)

        case _:
            return  # Invalid direction

def send(cmd):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(("localhost", 25566))
        s.sendall((cmd + "\n").encode("utf-8"))



def placeBlock(x, y, z):
    send(f"placeblock {x} {y} {z}")

def placeOffBlock(x, y, z):
    send(f"placeoffblock {x} {y} {z}")

def breakBlock(x, y, z):
    block = minescript.getblock(x, y, z)
    #print(block)
    if block != "minecraft:air":
        send(f"breakblock {x} {y} {z}")
        while block == "air":
            pass


def sendandread(cmd):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect(("localhost", 25566))
        s.sendall((cmd + "\n").encode())
        data = s.recv(8192).decode().strip()
        return json.loads(data) if data else {}

def getScoreboards():
    return sendandread("getscoreboards")

def getScoreboard(slot):
    return sendandread(f"getscoreboard {slot}")

def getBossbars():
    return sendandread("getbossbars")
