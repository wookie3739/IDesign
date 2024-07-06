import * as THREE from "../three.module.js";
import { deskColor } from "../objectConf/colors.js";

class Desk {
  constructor({
    x,
    z,
    object,
    width = 4,
    length = 2,
    depth = 2,
    color = deskColor,
    opacity = 1,
  }) {
    this.delete = this.deleteObj();
    const legWidth = 0.1;
    const legHeight = 2;
    const deskGroup = new THREE.Group();
    deskGroup.name = "DESK";

    // 상판
    const tableTopGeometry = new THREE.BoxGeometry(width, 0.1, length); //가로 두깨 새로
    const tableTopMaterial = new THREE.MeshBasicMaterial({
      color,
      transparent: true,
      opacity,
    });
    const tableTop = new THREE.Mesh(tableTopGeometry, tableTopMaterial);
    tableTop.position.y = legHeight;

    // 다리
    const legX = width / 2 - legWidth / 2;
    const legY = length / 2;
    const legZ = depth / 2 - legWidth;
    const legGeometry = new THREE.BoxGeometry(legWidth, length, legWidth);
    const legMaterial = new THREE.MeshBasicMaterial({
      color,
      transparent: true,
      opacity,
    });
    const leg1 = new THREE.Mesh(legGeometry, legMaterial);
    const leg2 = new THREE.Mesh(legGeometry, legMaterial);
    const leg3 = new THREE.Mesh(legGeometry, legMaterial);
    const leg4 = new THREE.Mesh(legGeometry, legMaterial);

    leg1.position.set(-legX, legY, -legZ);
    leg2.position.set(legX, legY, -legZ);
    leg3.position.set(-legX, legY, legZ);
    leg4.position.set(legX, legY, legZ);

    deskGroup.add(tableTop);
    deskGroup.add(leg1);
    deskGroup.add(leg2);
    deskGroup.add(leg3);
    deskGroup.add(leg4);
    // object 내에 생성위치 계산
    const bbox = new THREE.Box3().setFromObject(object);
    const minX = bbox.min.x + width / 2;
    const maxX = bbox.max.x - width / 2;
    const minZ = bbox.min.z + length / 2;
    const maxZ = bbox.max.z - length / 2;
    deskGroup.position.set(
      Math.max(minX, Math.min(maxX, x)),
      0,
      Math.max(minZ, Math.min(maxZ, z))
    );

    return deskGroup;
  }

  deleteObj = () => {
    // console.log("zz");
  };
}

export { Desk };
