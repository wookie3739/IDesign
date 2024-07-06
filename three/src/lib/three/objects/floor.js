import * as THREE from "../three.module.js";
import { floorName } from "../objectConf/objectNames.js";
import { floorColors } from "../objectConf/colors.js";
import { planeLength, planeWidth } from "../objectConf/length.js";

class Floor {
  constructor({
    x = 0,
    y = 0,
    z = 0,
    width = planeWidth,
    length = planeLength,
    floorColor = floorColors,
  }) {
    const floorGeometry = new THREE.PlaneGeometry(width, length);
    const floorMaterial = new THREE.MeshBasicMaterial({
      color: floorColor,
      side: THREE.DoubleSide,
    });
    const floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.name = floorName;
    floor.rotation.x = Math.PI / 2;
    floor.position.set(x, y, z);

    return floor;
  }
}

export { Floor };
