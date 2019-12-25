import socket
import json
import sys
import numpy as np
from search import Search

def main():
    host, port, color, timeout = parse_arg()
    client = Client(host, port)
    weights = {
        1:  [6, -0.1, -0.2, 0.1, 0.0, -0.2, -0.2, 0.67, 0.51, 0.18, 0.83],
        -1: [15, -0.2, -0.1, -0.1, -0.1, -0.2, -0.1, 0.27, 0.05, 0.68, 0.65]
    }
    search = Search(color, weights[color], float(timeout)-0.7, depth = 4)

    try:
        # present name
        client.send_name("Ragnarok")
        # wait init state
        state, turn = client.recv_state()
        # game loop:
        while True:
            if color == turn:
                move = search.start(state)
                if move != None:
                    client.send_move(move)
            state, turn = client.recv_state()

    finally:
        print('closing socket')
        client.close()


# TCP Client class
class Client:

    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))

    def send_name(self, name):
        encoded = name.encode("UTF-8")
        length = len(encoded).to_bytes(4, 'big')
        self.sock.sendall(length+encoded)

    def send_move(self, move):
        move_obj = {
            "from": chr(97 + move[0][1]) + str(move[0][0]+1),
            "to": chr(97 + move[1][1]) + str(move[1][0]+1)
        }
        encoded = json.dumps(move_obj).encode("UTF-8")
        length = len(encoded).to_bytes(4, 'big')
        self.sock.sendall(length+encoded)

    def recv_state(self):
        char = self.sock.recv(1)
        while(char == b'\x00'):
            char = self.sock.recv(1)
        length_str = char + self.sock.recv(1)
        total = int.from_bytes(length_str, "big")
        msg = self.sock.recv(total)
        state_obj = json.loads(msg.decode("UTF-8"))
        board = state_obj["board"]
        turn = 1 if state_obj["turn"] == "WHITE" else -1
        state_obj = list(board)
        for i in range(9):
            for j in range(9):
                if state_obj[i][j] == "BLACK":
                    state_obj[i][j] = -1
                if state_obj[i][j] == "WHITE":
                    state_obj[i][j] = 1
                if state_obj[i][j] == "KING":
                    state_obj[i][j] = 2
                if state_obj[i][j] == "EMPTY" or  state_obj[i][j] == "THRONE":
                    state_obj[i][j] = 0

        return state_obj, turn

    def close(self):
        self.sock.close()


# Argument parser
def parse_arg():

    def usage():
        print("Usage: client <white | black> timeout server_host")

    if len(sys.argv) != 4:
        usage()
        exit(1)

    color=sys.argv[1]
    if color == "White":
        return (sys.argv[3], 5800, 1, sys.argv[2])
    elif color == "Black":
        return (sys.argv[3], 5801, -1, sys.argv[2])
    else:
        usage()
        exit(1)

if __name__ == '__main__': main()