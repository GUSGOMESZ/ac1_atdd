<template>
  <div class="card">
    <h2>Processar Ranking Mensal</h2>
    <form @submit.prevent="processar">
      <div class="form-group">
        <label>Curso</label>
        <select v-model="cursoId" required>
          <option value="" disabled>Selecione um curso</option>
          <option v-for="c in cursos" :key="c.id" :value="c.id">{{ c.nome }} ({{ c.tipo }})</option>
        </select>
      </div>

      <div class="form-group">
        <label>Mínimo de comentários úteis</label>
        <input v-model.number="minimo" type="number" min="1" placeholder="3 (padrão)" />
      </div>

      <div style="display:flex; gap:10px; flex-wrap:wrap; margin-bottom:8px">
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Processando...' : 'Processar Ranking' }}
        </button>
      </div>

      <div v-if="msg" :class="['alert', erro ? 'alert-error' : 'alert-success']">{{ msg }}</div>
    </form>

    <div style="margin-top:16px; border-top:1px solid #eee; padding-top:16px">
      <h2 style="margin-bottom:12px">Cadastrar Curso</h2>
      <form @submit.prevent="criarCurso">
        <div style="display:flex; gap:10px; flex-wrap:wrap">
          <input v-model="novoCurso.nome" placeholder="Nome do curso" required />
          <input v-model="novoCurso.tipo" placeholder="Tipo (ex: Programação)" required />
          <button type="submit" class="btn btn-success btn-sm">Cadastrar</button>
        </div>
        <div v-if="msgCurso" class="alert alert-success" style="margin-top:8px">{{ msgCurso }}</div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const emit = defineEmits(['resultado'])

const cursos = ref([])
const cursoId = ref('')
const minimo = ref(3)
const loading = ref(false)
const msg = ref('')
const erro = ref(false)

const novoCurso = ref({ nome: '', tipo: '' })
const msgCurso = ref('')

async function carregarCursos() {
  const res = await fetch('/api/forum-rewards/cursos')
  cursos.value = await res.json()
}

async function processar() {
  if (!cursoId.value) return
  loading.value = true
  msg.value = ''
  try {
    const url = `/api/forum-rewards/ranking/process-custom?cursoId=${cursoId.value}&minimo=${minimo.value}`
    const res = await fetch(url, { method: 'POST' })
    if (!res.ok) throw new Error()
    const data = await res.json()
    msg.value = 'Ranking processado com sucesso!'
    erro.value = false
    emit('resultado', data)
  } catch {
    msg.value = 'Erro ao processar ranking.'
    erro.value = true
  } finally {
    loading.value = false
  }
}

async function criarCurso() {
  try {
    const res = await fetch('/api/forum-rewards/cursos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(novoCurso.value)
    })
    if (!res.ok) throw new Error()
    novoCurso.value = { nome: '', tipo: '' }
    msgCurso.value = 'Curso cadastrado!'
    setTimeout(() => { msgCurso.value = '' }, 2000)
    await carregarCursos()
  } catch {
    msgCurso.value = 'Erro ao cadastrar curso.'
  }
}

onMounted(carregarCursos)
</script>
