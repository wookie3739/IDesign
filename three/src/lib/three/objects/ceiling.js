import * as THREE from "../three.module.js";
import { ceilingColor } from "../objectConf/colors.js";
import {
  ceilingHeight,
  planeLength,
  planeWidth,
} from "../objectConf/length.js";
import { ceilingName } from "../objectConf/objectNames.js";
class Ceiling {
  constructor({
    x,
    y = ceilingHeight,
    z,
    width = planeWidth,
    length = planeLength,
    color = ceilingColor,
    rotationY,
  }) {
    const ceilingGeometry = new THREE.PlaneGeometry(width, length);
    const ceilingMaterial = new THREE.MeshBasicMaterial({
      color: color,
      side: THREE.BackSide,
    });
    const ceiling = new THREE.Mesh(ceilingGeometry, ceilingMaterial);
    ceiling.name = ceilingName;
    ceiling.rotation.x = Math.PI / 2;
    ceiling.rotation.y = rotationY;
    // ceiling.rotation.z = Math.PI / 4;
    ceiling.position.set(x, y, z);

    return ceiling;
  }
}

export { Ceiling };
