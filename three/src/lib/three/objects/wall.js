import { wallColor } from "../objectConf/colors.js";
import { ceilingHeight } from "../objectConf/length.js";
import { wallName } from "../objectConf/objectNames.js";
import * as THREE from "../three.module.js";

class Wall {
  constructor({
    x,
    y = 0,
    z,
    color = wallColor,
    width,
    length = ceilingHeight,
    rotationY = 0,
  }) {
    const wallGeometry = new THREE.PlaneGeometry(width, length);
    const wallMaterial = new THREE.MeshBasicMaterial({
      color: color,
      side: THREE.BackSide,
    });
    const wall = new THREE.Mesh(wallGeometry, wallMaterial);
    wall.name = wallName;
    wall.position.set(x, y, z);
    wall.rotation.y = rotationY;

    return wall;
  }
}

export { Wall };
