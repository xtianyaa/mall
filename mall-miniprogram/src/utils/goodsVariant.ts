import type { MallGoods, MallGoodsVariant } from "../types/mall";

/** 后端无 variant 表时注入的占位 SKU */
export function isSyntheticPlaceholderVariant(v: MallGoodsVariant): boolean {
  return v.id == null && Object.values(v.specs || {}).every((val) => !val || val === "默认规格" || val === "默认");
}

/** 真实可售 SKU（排除占位「默认规格」） */
export function getRealVariants(variants: MallGoodsVariant[] | undefined | null): MallGoodsVariant[] {
  if (!variants?.length) return [];
  if (variants.length === 1 && isSyntheticPlaceholderVariant(variants[0])) return [];
  return variants;
}

/** 是否需要在列表页加购时进入详情选规格（多 SKU） */
export function needsSkuPicker(goods: MallGoods): boolean {
  return getRealVariants(goods.variants).length > 1;
}

function norm(s: string | null | undefined): string {
  return (s ?? "").trim();
}

// ─────────────────────────── 类型 ───────────────────────────

/** none=无SKU, single=单一SKU(直接展示), flat=1维或多维平铺, matrix=2维矩阵 */
export type VariantUiMode = "none" | "single" | "flat" | "matrix";

export interface SpecChip {
  value: string;
  display: string;
  disabled: boolean;
}

export interface SpecDim {
  name: string;
  chips: SpecChip[];
}

export interface VariantUiPlan {
  mode: VariantUiMode;
  /** 维度名列表（按顺序），来自后端 specNames */
  specNames: string[];
  /** 每维度及其可选值（flat 模式/2+维） */
  dims: SpecDim[];
  /** 仅 matrix 模式：key=dim1选中值，value=dim2 可选值列表 */
  dim2ByDim1?: Record<string, string[]>;
}

// ─────────────────────────── 构建 UI Plan ───────────────────────────

/**
 * 根据 SKU 数据推断展示模式与维度信息。
 * flat：1维（单列chip）或3+维（多列chip均展示）
 * matrix：恰好2维（颜色×尺码式交叉矩阵）
 */
export function buildVariantUiPlan(
  variants: MallGoodsVariant[],
  specNamesFromGoods?: string[]
): VariantUiPlan {
  const list = getRealVariants(variants);

  if (list.length === 0) {
    return { mode: "none", specNames: [], dims: [] };
  }

  if (list.length === 1) {
    const names = list[0].specNames?.length ? list[0].specNames : (specNamesFromGoods || ["规格"]);
    return { mode: "single", specNames: names, dims: [] };
  }

  // 多 SKU：以第一个 variant 的 specNames 为准
  const specNames = list[0].specNames?.length ? list[0].specNames : (specNamesFromGoods || []);

  if (specNames.length === 1) {
    // 1维 → flat
    const dimName = specNames[0];
    const dimVals = [...new Set(list.map((v) => v.specs?.[dimName]).filter(Boolean))] as string[];
    return { mode: "flat", specNames, dims: [{ name: dimName, chips: [] }] };
  }

  if (specNames.length === 2) {
    // 2维 → matrix
    const [d1, d2] = specNames;
    const d1Vals = [...new Set(list.map((v) => v.specs?.[d1]).filter(Boolean))] as string[];
    const dim2ByDim1: Record<string, string[]> = {};
    for (const v of list) {
      const k = v.specs?.[d1];
      if (!k) continue;
      if (!dim2ByDim1[k]) dim2ByDim1[k] = [];
      const dv2 = v.specs?.[d2];
      if (dv2 && !dim2ByDim1[k].includes(dv2)) dim2ByDim1[k].push(dv2);
    }
    return {
      mode: "matrix",
      specNames,
      dims: [{ name: d1, chips: [] }, { name: d2, chips: [] }],
      dim2ByDim1,
    };
  }

  // 3+ 维 → flat（多列 chip 并排）
  const dims = specNames.map((name) => ({
    name,
    chips: [] as SpecChip[],
  }));
  return { mode: "flat", specNames, dims };
}

// ─────────────────────────── 填充选项状态 ───────────────────────────

/** 检查给定维度组合是否有库存 */
function variantHasStock(
  variants: MallGoodsVariant[],
  selectedSpecs: Record<string, string>
): boolean {
  const list = getRealVariants(variants);
  return list.some((v) => {
    const match = Object.entries(selectedSpecs).every(
      ([k, val]) => norm(v.specs?.[k]) === norm(val)
    );
    return match && (v.stock ?? 0) > 0;
  });
}

/**
 * 填充 chip.disabled 状态。
 * selectedSpecs：当前已选规格 key→value
 */
export function fillSpecChips(
  variants: MallGoodsVariant[],
  plan: VariantUiPlan,
  selectedSpecs: Record<string, string>
): SpecDim[] {
  const list = getRealVariants(variants);

  if (plan.mode === "none" || plan.mode === "single") return [];

  if (plan.mode === "matrix") {
    const [d1, d2] = plan.specNames;
    const d1Vals = [...new Set(list.map((v) => v.specs?.[d1]).filter(Boolean))] as string[];
    const selectedD1 = selectedSpecs[d1];

    // dim1 chips
    const dim1Chips: SpecChip[] = d1Vals.map((val) => ({
      value: val,
      display: val,
      disabled: !list.some((v) => v.specs?.[d1] === val && (v.stock ?? 0) > 0),
    }));

    // dim2 chips（按当前 d1 过滤）
    const d2ValsForSelected = selectedD1 ? (plan.dim2ByDim1?.[selectedD1] || []) : [];
    const dim2Chips: SpecChip[] = d2ValsForSelected.map((val) => ({
      value: val,
      display: val,
      disabled: selectedD1
        ? !variantHasStock(list, { ...selectedSpecs, [d2]: val })
        : true,
    }));

    return [
      { name: d1, chips: dim1Chips },
      { name: d2, chips: dim2Chips },
    ];
  }

  // flat（1维或多维平铺）
  return plan.dims.map((dim) => {
    const dimVals = [...new Set(list.map((v) => v.specs?.[dim.name]).filter(Boolean))] as string[];
    const chips: SpecChip[] = dimVals.map((val) => ({
      value: val,
      display: val,
      disabled: !variantHasStock(list, { ...selectedSpecs, [dim.name]: val }),
    }));
    return { name: dim.name, chips };
  });
}

// ─────────────────────────── 选中态解析 ───────────────────────────

/** 根据已选规格（key→value）查找匹配的 variant */
export function findVariantBySpecs(
  variants: MallGoodsVariant[],
  selectedSpecs: Record<string, string>
): MallGoodsVariant | null {
  const list = getRealVariants(variants);
  if (!list.length) return null;

  // 优先返回有库存的
  return (
    list.find(
      (v) =>
        Object.entries(selectedSpecs).every(
          ([k, val]) => norm(v.specs?.[k]) === norm(val)
        ) && (v.stock ?? 0) > 0
    ) ||
    list.find((v) =>
      Object.entries(selectedSpecs).every(
        ([k, val]) => norm(v.specs?.[k]) === norm(val)
      )
    ) ||
    null
  );
}

/** 从某个 SKU 初始化选中态（仅适用于 matrix/flat 已知维度名时） */
export function selectionFromVariant(
  v: MallGoodsVariant | null | undefined
): Record<string, string> {
  if (!v?.specs) return {};
  const result: Record<string, string> = {};
  for (const [k, val] of Object.entries(v.specs)) {
    if (val) result[k] = val;
  }
  return result;
}

/** 提示文案 */
export function buildSelectHint(
  plan: VariantUiPlan,
  selectedSpecs: Record<string, string>,
  filledGroups: number
): string {
  if (plan.mode === "none" || plan.mode === "single") return "";
  if (filledGroups >= plan.specNames.length) return "";

  // 找第一个未选的维度名
  const missingDim = plan.specNames.find((name) => !selectedSpecs[name]);
  if (!missingDim) return "";
  if (plan.mode === "matrix" && filledGroups === 1) {
    return `请选择${missingDim}`;
  }
  return `请选择${missingDim}`;
}
