import * as THREE from "../three.module.js";
import { roomName } from "../objectConf/objectNames.js";
import {
  ceilingHeight,
  planeLength,
  planeWidth,
} from "../objectConf/length.js";
import { Floor } from "./floor.js";
import { Ceiling } from "./ceiling.js";
import { Wall } from "./wall.js";
import { Resizer } from "./resizer.js";

class Room {
  constructor({
    x = 0,
    y = 0,
    z = 0,
    width = planeWidth,
    length = planeLength,
  }) {
    const room = new THREE.Group();
    room.name = roomName;

    const floor = new Floor({ x, y, z, width, length });
    room.add(floor);
    const ceil = new Ceiling({ x, z, rotationY: -Math.PI / 1 });
    room.add(ceil);
    const westwall = new Wall({
      x,
      z: z + length / 2,
      y: ceilingHeight / 2 + y,
      width,
      length: ceilingHeight,
    });
    const eastwall = new Wall({
      x,
      z: z - length / 2,
      y: ceilingHeight / 2 + y,
      width,
      length: ceilingHeight,
      rotationY: -Math.PI / 1,
    });
    const southwall = new Wall({
      x: x - width / 2,
      z: z,
      y: ceilingHeight / 2 + y,
      width: length,
      length: ceilingHeight,
      rotationY: -Math.PI / 2,
    });
    const northwall = new Wall({
      x: x + width / 2,
      z: z,
      y: ceilingHeight / 2 + y,
      width: length,
      length: ceilingHeight,
      rotationY: Math.PI / 2,
    });

    room.add(westwall);
    room.add(eastwall);
    room.add(southwall);
    room.add(northwall);

    const resizer = new Resizer({ width, length, floor });

    room.add(resizer);
    return room;
  }
}

export { Room };
