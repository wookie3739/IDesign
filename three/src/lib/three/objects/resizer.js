import { resizerName } from "../objectConf/objectNames";
import * as THREE from "../three.module.js";

class Resizer {
  constructor({ width, length, floor }) {
    const resizer = new THREE.Group();
    resizer.name = resizerName;
    const rsGeometry = new THREE.BoxGeometry(1, 1);
    const rsMaterial = new THREE.MeshBasicMaterial({
      color: "red",
      side: THREE.DoubleSide,
    });
    // const resizer = new THREE.Mesh(rsGeometry, rsMaterial);
    // resizer.position.copy(floor.position);
    // resizer.rotation.copy(floor.rotation);
    // resizer.name = resizerName;
    // return resizer;

    // 각 꼭지점 계산
    const halfWidth = width / 2;
    const halfLength = length / 2;

    const vertices = [
      new THREE.Vector3(-halfWidth, 0, -halfLength), // Bottom-left
      new THREE.Vector3(halfWidth, 0, -halfLength), // Bottom-right
      new THREE.Vector3(halfWidth, 0, halfLength), // Top-right
      new THREE.Vector3(-halfWidth, 0, halfLength), // Top-left
    ];

    vertices.forEach((vertex) => {
      vertex.applyMatrix4(floor.matrixWorld);
    });

    // const box1 = new THREE.Mesh(rsGeometry, rsMaterial);
    // const box2 = new THREE.Mesh(rsGeometry, rsMaterial);
    // const box3 = new THREE.Mesh(rsGeometry, rsMaterial);
    // const box4 = new THREE.Mesh(rsGeometry, rsMaterial);
    // box1.position.set(-7.5, 0, -5);
    // box2.position.set(7.5, 0, -5);
    // box3.position.set(7.5, 0, 5);
    // box4.position.set(-7.5, 0, 5);

    // resizer.add(box1);
    // resizer.add(box2);
    // resizer.add(box3);
    // resizer.add(box4);
    vertices.forEach((v) => {
      console.log(v.x, v.y, v.z);
      const box = new THREE.Mesh(rsGeometry, rsMaterial);
      box.position.set(v.x, v.y, v.z);
      resizer.add(box);
      //   const resizerChild = new THREE.Mesh(rsGeometry, rsMaterial);
      //   resizer.position.set(v.x, v.y, v.z);
      //   resizer.add(resizerChild);
    });

    resizer.visible = false;
    return resizer;
  }
}

export { Resizer };
